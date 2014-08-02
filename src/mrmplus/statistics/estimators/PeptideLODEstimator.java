/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.estimators;

import mrmplus.statistics.mappers.ExpICalibrationPointToRecordsMapper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import mrmplus.PeptideRecord;
import mrmplus.statistics.mappers.PeptideTransitionToRecordsMapper;
import mrmplus.enums.PeptideRecordsType;
import mrmplus.enums.PeptideResultOutputType;
import mrmplus.statistics.resultobjects.DetectionLevelPriorityQueue;
import mrmplus.statistics.resultobjects.LimitOfDetection;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

/**
 *
 * @author paiyeta1
 */
public class PeptideLODEstimator {

    
    public LinkedList<LimitOfDetection> estimateLOD(LinkedList<PeptideRecord> peptideRecords, 
                                                        PeptideResultOutputType peptideResultOutputType,
                                                            HashMap<String, Double> pointToDilutionMap,
                                                                HashMap<String, String> config,
                                                                    PrintWriter logWriter) throws FileNotFoundException, 
                                                                                                            IOException {
        //throw new UnsupportedOperationException("Not yet implemented");
        LinkedList<LimitOfDetection> lods = new LinkedList<LimitOfDetection>();
        switch (peptideResultOutputType){
            
            case TRANSITIONS:
                TransitionsLODEstimator tLODE = new TransitionsLODEstimator();
                lods = tLODE.estimateLimitsOfDetections(peptideRecords, config, logWriter);
                break;
            
            case SUMMED:
                // uses all records mapped to peptide irrespective of  
                // getBlanks before the first curve
                
                // Instantiate a transitionsLODEstimator to derive the lowest cali_Point at which an
                // an LOD can be estimated across all transitions...
                TransitionsLODEstimator sumtLODE = new TransitionsLODEstimator();
                MinimumDetectionObject mDObj = sumtLODE.estimateMinDetectionCaliPointAcross(peptideRecords, config, logWriter);
                int minCaliPoint = mDObj.getMinDetectionCaliPointAcross();                
                
                LimitOfDetection summedLOD;
                LinkedList<PeptideRecord> summedTransitionsDerivedPeptideRecords;
                // test minCaliPointAcross and attend appropriately to minimum level of detection...
                switch (minCaliPoint) {
                    
                    case Integer.MAX_VALUE: //in which case no detectable minimum LOD value across all calibration points
                        logWriter.println("      no detectable limit found for summed LOD value estimation...");
                        summedLOD = new LimitOfDetection("SUMMED",0,0,0,false);
                                        // the five parameters LOD constructor... 
                                        // LimitOfDetection(String transitionID, double average, 
                                        //                  double sd, double lod, boolean usedSpikedInConcentration)                        
                        summedLOD.setConcentrationPointUsed("NA");
                        summedLOD.setUnDetectable(true);
                        summedLOD.setZeroValueFlag(false);
                        break;
                    
                    case 0: // in which case there are detection values in the preCurveBlanks
                        logWriter.println("      minimum point detection found in pre-curve blanks for summed LOD value estimation...");
                        // therefore, get the preCurve peptide records for computation....
                        int preCurveBlanks = Integer.parseInt(config.get("preCurveBlanks"));
                        LinkedList<PeptideRecord> preFirstCurveBlanks = getPreFirstCurveBlanks(peptideRecords, preCurveBlanks);
                        logWriter.println("        " + preFirstCurveBlanks.size() + " pre-first curve blanks");
                        //LimitOfDetection summedLOD;
                        summedTransitionsDerivedPeptideRecords = sumEachReplicateRunTransitions(preFirstCurveBlanks, 
                                                                                    PeptideRecordsType.PRECURVEBLANKS, 
                                                                                        pointToDilutionMap, 
                                                                                            config,
                                                                                                logWriter,
                                                                                                    minCaliPoint);
                        summedLOD = computeLOD(summedTransitionsDerivedPeptideRecords);
                                // NOTE: the method computeLOD() returns the four parameters LOD initiated object from its computed values
                                //          new LimitOfDetection(average,sdvalue,lodValue, hasZeroValue);
                                //          == LimitOfDetection(double average, double sd, double lod, boolean zeroFlagged)
                        // update other LOD object attributes
                        summedLOD.setTransitionID("SUMMED");
                        summedLOD.setConcentrationPointUsed("preCurveBlanks");
                        summedLOD.setUnDetectable(false);
                        summedLOD.setUsedSpikedInConcentration(false);
                        break;
                    
                    default: // min detection point is beyond preCurveBlanks
                        logWriter.println("      no minimum point detection found in pre-curve blanks for summed LOD value estimation...");
                        // therefore utilize the values at the minimum detection point for LOD estimation.
                        //use standard deviation of signal observed/detected in lowest spiked sample 
                        String caliPoint = "Point_" + minCaliPoint;
                        LinkedList<PeptideRecord> spikedSampleRecords = getSpikedSampleRecords(peptideRecords, caliPoint);
                        summedTransitionsDerivedPeptideRecords = 
                            sumEachReplicateRunTransitions(spikedSampleRecords, 
                                                            PeptideRecordsType.SPIKEDCONCENTRATION, 
                                                                pointToDilutionMap, 
                                                                    config,
                                                                        logWriter,
                                                                            minCaliPoint);
                        summedLOD = computeLOD(summedTransitionsDerivedPeptideRecords);
                                // NOTE: the method computeLOD() returns the four parameters LOD initiated object from its computed values
                                //          new LimitOfDetection(average,sdvalue,lodValue, hasZeroValue);
                                //          == LimitOfDetection(double average, double sd, double lod, boolean zeroFlagged)                        
                        summedLOD.setTransitionID("SUMMED");
                        summedLOD.setConcentrationPointUsed(caliPoint);
                        summedLOD.setUnDetectable(false);
                        summedLOD.setUsedSpikedInConcentration(true);
                        break;
                       //
                }
                                
                lods.add(summedLOD);
                break;
            
            default: // BOTH
                // estimate LOD per transition...
                LinkedList<LimitOfDetection> transitionsLODs = 
                        estimateLOD(peptideRecords, 
                                        PeptideResultOutputType.TRANSITIONS, 
                                            pointToDilutionMap, 
                                                config, logWriter);
                // add estimated transition-lods to lods
                for(LimitOfDetection transitionLOD : transitionsLODs){
                    lods.add(transitionLOD);
                }                
                // estimate summed transitionPreCurveDerivedLOD...
                LinkedList<LimitOfDetection> summedLODs = 
                        estimateLOD(peptideRecords, 
                                        PeptideResultOutputType.SUMMED, 
                                            pointToDilutionMap, 
                                                config, logWriter);
                // add summed-transitionPreCurveDerivedLOD to lods 
                for(LimitOfDetection sumLOD : summedLODs){
                    lods.add(sumLOD);
                }
            
        }
                       
        return lods;
    }

    private LinkedList<PeptideRecord> getPreFirstCurveBlanks(LinkedList<PeptideRecord> transitionRecords, int preCurveBlanks) {
        //throw new UnsupportedOperationException("Not yet implemented");
        LinkedList<PeptideRecord> preCurves = new LinkedList<PeptideRecord>();
        for(int runOrder = 1; runOrder <= preCurveBlanks; runOrder++){
            for(PeptideRecord record : transitionRecords){
                if(Integer.parseInt(record.getRunOrder()) == runOrder){
                    preCurves.add(record);
                }
            }
        }      
        return preCurves;
    }

    private LimitOfDetection computeLOD(LinkedList<PeptideRecord> preFirstCurveBlanks) {
        //throw new UnsupportedOperationException("Not yet implemented");
        LimitOfDetection lod;
        //determined from blanks injected before first curve using average plus 3x standard deviation of blank signal
        double[] values = new double[preFirstCurveBlanks.size()];
        for(int i = 0; i < values.length; i++){
            values[i] = preFirstCurveBlanks.get(i).getMeasuredConcentration();//alternately, peakArea ratio may be used.
        }
        
        boolean hasZeroValue = hasZeroValue(values);       
        
        //evaluate mean
        Mean mean = new Mean();
        double average = mean.evaluate(values);
        //evaluate sd
        StandardDeviation sd = new StandardDeviation();
        double sdvalue = sd.evaluate(values);
        
        //lod
        double lodValue = (average + (sdvalue * 3));
        lod = new LimitOfDetection(average,sdvalue,lodValue, hasZeroValue);
        return lod;
    }

    private LinkedList<PeptideRecord> getSpikedSampleRecords(LinkedList<PeptideRecord> peptideRecords, String caliPoint) {
        LinkedList<PeptideRecord> spikedSampleRecords = new LinkedList<PeptideRecord>();
        //get the lowest spikedIn Value
        for(PeptideRecord record: peptideRecords){
            if(record.getCalibrationPoint().equalsIgnoreCase(caliPoint)){
                spikedSampleRecords.add(record);
            }
        }
        return spikedSampleRecords;
    }

    private boolean hasZeroValue(double[] values) {
        //throw new UnsupportedOperationException("Not yet implemented");
        boolean hasZero = false;
        for(double value : values){
            if(value == 0){
                hasZero = true;
                break;
            }
        }
        
        return hasZero;
    }

    /*
     * Method sums each replicate run top transitions' peak area into a summed value
     * in a PeptideRecord
     * 
     * @params preFrstCurveBlanks
     * @return summedReplicateRunTransitions
     * 
     */
    private LinkedList<PeptideRecord> sumEachReplicateRunTransitions(LinkedList<PeptideRecord> peptideRecords,
                                                            PeptideRecordsType pRecType,
                                                                HashMap<String, Double> pointToDilutionMap,
                                                                    HashMap<String,String> config,
                                                                        PrintWriter logWriter,
                                                                        int caliPoint) throws FileNotFoundException, IOException {
        //throw new UnsupportedOperationException("Not yet implemented");
        LinkedList<PeptideRecord> summedReplicateRunTransitions = new LinkedList<PeptideRecord>();
        switch(pRecType){
            case PRECURVEBLANKS:
                //since precurve blanks do not have "Replicate" attribute associated with them,
                //peptideRecords are mapped to their respective replicateID and summed
                logWriter.println("       summing pre-curve blanks...");
                int preCurveBlanksNumber = Integer.parseInt(config.get("preCurveBlanks"));
                HashMap<Integer, LinkedList<PeptideRecord>> runToRecordsMap = new HashMap<Integer, LinkedList<PeptideRecord>>();
                for(PeptideRecord peptideRecord : peptideRecords){
                    int runOrder = Integer.parseInt(peptideRecord.getRunOrder());
                    if(runOrder <= preCurveBlanksNumber){
                        //check record mapping in runToRecordsMap
                        if(runToRecordsMap.containsKey(runOrder)){
                            LinkedList<PeptideRecord> mappedRecords = runToRecordsMap.remove(runOrder);
                            mappedRecords.add(peptideRecord);
                            runToRecordsMap.put(runOrder, mappedRecords);
                        }else{
                            LinkedList<PeptideRecord> mappedRecords = new LinkedList<PeptideRecord>();
                            mappedRecords.add(peptideRecord);
                            runToRecordsMap.put(runOrder, mappedRecords);
                        }
                    }
                }
                //sum peptideRecords for each preCurveBlankNumber
                PeptideRecordsSummer prs = new PeptideRecordsSummer();
                Set<Integer> preCurveNumbers = runToRecordsMap.keySet();
                for(int preCurveNumber : preCurveNumbers){
                    LinkedList<PeptideRecord> preCurveNumberMappedBlanks = runToRecordsMap.get(preCurveNumber);
                    logWriter.println("        " + preCurveNumberMappedBlanks.size() + 
                            " peptide records map to precurve run number " + preCurveNumber);               
                    PeptideRecord preCurveNumberSummedPeptideRecord = prs.sumPeptideRecords(preCurveNumberMappedBlanks);
                    //update other summed Peptide attributes...
                    //...
                    summedReplicateRunTransitions.add(preCurveNumberSummedPeptideRecord);
                }              
                break;
                
            case SPIKEDCONCENTRATION:
                //map each peptideRecord(s) to replicate type: expectedly, number of 
                logWriter.println("       summing minimum spikedIn concentration transitions...");
                HashMap<String, LinkedList<PeptideRecord>> replicateToRecordsMap = new HashMap<String, LinkedList<PeptideRecord>>();
                logWriter.println("         mapping replicate Id to peptide records...");
                for(PeptideRecord minSpikedInConcPeptideRecord : peptideRecords){
                    String replicateID = minSpikedInConcPeptideRecord.getReplicate();
                    if(replicateToRecordsMap.containsKey(replicateID)){
                        LinkedList<PeptideRecord> mappedRecords = replicateToRecordsMap.remove(replicateID);
                        mappedRecords.add(minSpikedInConcPeptideRecord);
                        replicateToRecordsMap.put(replicateID, mappedRecords);
                    }else{
                        LinkedList<PeptideRecord> mappedRecords = new LinkedList<PeptideRecord>();
                        mappedRecords.add(minSpikedInConcPeptideRecord);
                        replicateToRecordsMap.put(replicateID, mappedRecords);
                    }
                   
                }
                //sum peptideRecords for each replicate
                PeptideRecordsSummer sprs = new PeptideRecordsSummer();
                Set<String> replicateIDs = replicateToRecordsMap.keySet();
                logWriter.println("         " + replicateIDs.size() + " replicate Id's found...");
                for(String replicateID : replicateIDs){
                    LinkedList<PeptideRecord> replicateMappedPeptideRecords = replicateToRecordsMap.get(replicateID);
                    logWriter.println("          " + replicateMappedPeptideRecords.size() + " peptide records mapped to " +
                                                        replicateID + " replicate Id");
                    PeptideRecord replicateSummedPeptideRecord = sprs.sumPeptideRecords(replicateMappedPeptideRecords);
                    //update other summed Peptide attributes...
                    //set dilution
                    replicateSummedPeptideRecord.setDilution(pointToDilutionMap.get("Point_" + caliPoint)); //minimum_spikedIn concentration.
                    replicateSummedPeptideRecord.setCalibrationPoint("Point_" + caliPoint);
                    //...
                    summedReplicateRunTransitions.add(replicateSummedPeptideRecord);
                }  
                break;           
        }
        //HashMap<String
        return summedReplicateRunTransitions;
    }
   
    private class TransitionsLODEstimator {       
        
        public MinimumDetectionObject estimateMinDetectionCaliPointAcross(LinkedList<PeptideRecord> peptideRecords,
                                                        HashMap<String, String> config,
                                                                PrintWriter logWriter) {
            //estimate LOD per transition...
            logWriter.println("    transition(s) associated LOD estimation...");
            PeptideTransitionToRecordsMapper transToRecordsMapper = new PeptideTransitionToRecordsMapper();
            HashMap<String, LinkedList<PeptideRecord>> transitionToRecords = 
                    transToRecordsMapper.mapTransitionsToRecords(peptideRecords);
            
            Set<String> transitions = transitionToRecords.keySet();
            logWriter.println("    " + transitions.size() + " transitions found associated with peptide " + 
                    peptideRecords.getFirst().getPeptideSequence());

            //Instantiate a HashMap object to hold transitions and mapped/estimatedDetections 
            // from PreCurveBlanks to maximum spiked-in concentration level...
            HashMap<String,DetectionLevelPriorityQueue> transitionToDetectionQueue = 
                    new HashMap<String,DetectionLevelPriorityQueue>();
            
             for(String transition : transitions){
                //for each transition,
                LinkedList<PeptideRecord> transitionRecords = transitionToRecords.get(transition);
                logWriter.println("     to transition " + transition + ", " + transitionRecords.size() + 
                                                            " peptide records were mapped...");
                //getBlanks before the first curve
                int preCurveBlanks = Integer.parseInt(config.get("preCurveBlanks"));
                LinkedList<PeptideRecord> preFirstCurveBlanks = getPreFirstCurveBlanks(transitionRecords, preCurveBlanks);
                logWriter.println("      " + preFirstCurveBlanks.size() + " pre-first curve blanks");
                LimitOfDetection transitionPreCurveDerivedLOD;
                //computeLOD for preCurveBlanks...
                transitionPreCurveDerivedLOD = computeLOD(preFirstCurveBlanks);
                               // NOTE: the method computeLOD() returns the four parameters LOD initiated object from its computed values
                                //          new LimitOfDetection(average,sdvalue,lodValue, hasZeroValue);
                                //          == LimitOfDetection(double average, double sd, double lod, boolean zeroFlagged)                                        
                // update attributes
                transitionPreCurveDerivedLOD.setUsedSpikedInConcentration(false);
                transitionPreCurveDerivedLOD.setTransitionID(transition);
                transitionPreCurveDerivedLOD.setConcentrationPointUsed("preCurveBlanks");
                // insert the transitionDetectionLevel object into map
                DetectionLevelPriorityQueue dLPQueue = new DetectionLevelPriorityQueue(transitionPreCurveDerivedLOD);

                // map calibration points to records (which are repectively from the [three] replicates)
                ExpICalibrationPointToRecordsMapper pointToRecordsMapper = 
                                        new ExpICalibrationPointToRecordsMapper();
                HashMap<String,LinkedList<PeptideRecord>> caliPointToRecords = 
                                        pointToRecordsMapper.mapCalibrationPointsToRecords(transitionRecords);

                // for each of the calibration point, estimate detection Limit
                Set<String> caliPoints = caliPointToRecords.keySet();
                for(String caliPoint : caliPoints){
                    LinkedList<PeptideRecord> caliPointMappedPeptideRecords = caliPointToRecords.get(caliPoint);
                    LimitOfDetection caliPointLOD = computeLOD(caliPointMappedPeptideRecords);
                               // NOTE: the method computeLOD() returns the four parameters LOD initiated object from its computed values
                                //          new LimitOfDetection(average,sdvalue,lodValue, hasZeroValue);
                                //          == LimitOfDetection(double average, double sd, double lod, boolean zeroFlagged)                                            
                    caliPointLOD.setConcentrationPointUsed(caliPoint);
                    caliPointLOD.setUsedSpikedInConcentration(true);
                    caliPointLOD.setTransitionID(transition);
                    dLPQueue.insert(caliPointLOD);
                }                   
                transitionToDetectionQueue.put(transition, dLPQueue);                
            }

            // Determine minimum calibration point wherein there is a quantifiable LOD across peptide transitions
            LinkedList<Integer> detectionCaliPoints = new LinkedList<Integer>();
            for(String transition : transitions){
                DetectionLevelPriorityQueue tDLQueue = transitionToDetectionQueue.get(transition);
                // check if there is any detection
                double detectionLimit = 0;
                do{
                    detectionLimit = tDLQueue.peek().getLimitOfDetection();
                    if(detectionLimit == 0){
                        tDLQueue.pop();
                    }
                }while((detectionLimit == 0) && (tDLQueue.size() != 0));

                // check
                if(tDLQueue.size() == 0){ //undetectable peptide transition
                    detectionCaliPoints.add(Integer.MAX_VALUE);
                } else {
                    // get minimum calibration point
                    detectionCaliPoints.add(tDLQueue.peek().getCalibrationPointUsed());
                }
            }
            // the maximum of the detectionCaliPoints is the minimum concenteration for which all transitions are detectable
            int minDetectionCaliPointAcross = estimateMinDetectionCaliPointAcrossHelper(detectionCaliPoints); 
            return new MinimumDetectionObject(minDetectionCaliPointAcross, transitionToDetectionQueue);
        }
        
        private int estimateMinDetectionCaliPointAcrossHelper(LinkedList<Integer> detectionCaliPoints) {
            //throw new UnsupportedOperationException("Not yet implemented");
            // NB. the maximum of the detectionCaliPoints is the minimum concenteration for which all transitions are detectable
            int max = detectionCaliPoints.getFirst();
            for(int detectionCaliPoint : detectionCaliPoints){
                if(detectionCaliPoint > max){
                    max = detectionCaliPoint;
                }
            }
            return max;
        }

        public LinkedList<LimitOfDetection> estimateLimitsOfDetections(LinkedList<PeptideRecord> peptideRecords,
                                                        HashMap<String, String> config,
                                                                PrintWriter logWriter) {
            LinkedList<LimitOfDetection> lods = new LinkedList<LimitOfDetection>();
            MinimumDetectionObject mDetection = 
                    estimateMinDetectionCaliPointAcross(peptideRecords, config, logWriter);
            
            int minDetectionCaliPointAcross = mDetection.getMinDetectionCaliPointAcross();
            HashMap<String,DetectionLevelPriorityQueue> transitionToDetectionQueue = 
                    mDetection.getTransitionToDetectionQueue();
            //throw new UnsupportedOperationException("Not yet implemented");
            Set<String> transitions = transitionToDetectionQueue.keySet();
            if(minDetectionCaliPointAcross == Integer.MAX_VALUE){ //undetectable transition across...
                for(String transition : transitions){
                    LimitOfDetection transitionLOD = new LimitOfDetection(transition,0,0,0,false);
                                        //LimitOfDetection(String transitionID, double average, 
                                        //                  double sd, double lod, boolean usedSpikedInConcentration)
                    transitionLOD.setConcentrationPointUsed("NA");
                    transitionLOD.setUnDetectable(true);
                    //transitionLOD.setUsedSpikedInConcentration(false);
                    transitionLOD.setZeroValueFlag(false);
                    lods.add(transitionLOD);

                }                   
            }else{
                for(String transition : transitions){
                    //get the LOD objects at the minDetectionCaliPointAcross
                    DetectionLevelPriorityQueue tDLQueue = transitionToDetectionQueue.get(transition);
                    //int caliPoint;
                    LimitOfDetection lod;
                    do{
                        lod = tDLQueue.pop();
                    } while (lod.getCalibrationPointUsed() < minDetectionCaliPointAcross);

                    lods.add(lod);
                }   
            }
            return lods;
        }  
    }
    
    private class MinimumDetectionObject {

        private int minDetectionCaliPointAcross;
        private HashMap<String, DetectionLevelPriorityQueue> transitionToDetectionQueue;
        
        public MinimumDetectionObject(int minDetectionCaliPointAcross, 
                HashMap<String, DetectionLevelPriorityQueue> transitionToDetectionQueue) {
            this.minDetectionCaliPointAcross = minDetectionCaliPointAcross;
            this.transitionToDetectionQueue = transitionToDetectionQueue;       
        }

        private HashMap<String, DetectionLevelPriorityQueue> getTransitionToDetectionQueue() {
            return transitionToDetectionQueue;
        }

        private int getMinDetectionCaliPointAcross() {
            return minDetectionCaliPointAcross;
        }
    }

}
