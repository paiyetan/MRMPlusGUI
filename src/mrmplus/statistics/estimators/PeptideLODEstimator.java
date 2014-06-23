/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.estimators;

import ios.DilutionFileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import mrmplus.PeptideRecord;
import mrmplus.PeptideTransitionToRecordsMapper;
import mrmplus.enums.PeptideRecordsType;
import mrmplus.enums.PeptideResultOutputType;
import mrmplus.statistics.resultobjects.LimitOfDetection;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

/**
 *
 * @author paiyeta1
 */
public class PeptideLODEstimator {

    public LinkedList<LimitOfDetection> estimateLOD(
                                                    //LinkedList<PeptideResult> peptideResults,
                                                    LinkedList<PeptideRecord> peptideRecords, 
                                                    PeptideResultOutputType peptideResultOutputType,
                                                    HashMap<String, Double> pointToDilutionMap,
                                                    HashMap<String, String> config,
                                                    PrintWriter logWriter) throws FileNotFoundException, IOException {
        //throw new UnsupportedOperationException("Not yet implemented");
        LinkedList<LimitOfDetection> lods = new LinkedList<LimitOfDetection>();
        switch (peptideResultOutputType){
            
            case TRANSITIONS:
                //estimate LOD per transition...
                logWriter.println("    transition(s) associated LOD estimation...");
                PeptideTransitionToRecordsMapper transToRecordsMapper = new PeptideTransitionToRecordsMapper();
                HashMap<String, LinkedList<PeptideRecord>> transitionToRecords = 
                        transToRecordsMapper.mapTransitionsToRecords(peptideRecords);
                Set<String> transitions = transitionToRecords.keySet();
                logWriter.println("    " + transitions.size() + " transitions found associated with peptide " + 
                        peptideRecords.getFirst().getPeptideSequence());
                for(String transition : transitions){
                    //for each transition,
                    LinkedList<PeptideRecord> transitionRecords = transitionToRecords.get(transition);
                    logWriter.println("     to transition " + transition + ", " + transitionRecords.size() + 
                                                              " peptide records were mapped...");
                    //getBlanks before the first curve
                    int preCurveBlanks = Integer.parseInt(config.get("preCurveBlanks"));
                    LinkedList<PeptideRecord> preFirstCurveBlanks = getPreFirstCurveBlanks(transitionRecords, preCurveBlanks);
                    logWriter.println("      " + preFirstCurveBlanks.size() + " pre-first curve blanks");
                    LimitOfDetection transitionLOD;
                    transitionLOD = computeLOD(preFirstCurveBlanks);
                    // update attributes
                    transitionLOD.setUsedMinSpikedInConcentration(false);
                    transitionLOD.setTransitionID(transition);
                    
                    // however, if no detectable signal is observed in blanks... 
                    if(transitionLOD.getLimitOfDetection() <= 0){
                        logWriter.println("       found no detectable signal in blanks...");
                        //use standard deviation of signal observed/detected in lowest spiked sample 
                        logWriter.println("       computing LOD in minimum spiked-In sample...");
                        LinkedList<PeptideRecord> lowestSpikedSampleRecords = getLowestSpikedSampleRecords(transitionRecords);
                        logWriter.println("        " + lowestSpikedSampleRecords.size() + " peptide records found at lowest spiked-in concentration...");
                        transitionLOD = computeLOD(lowestSpikedSampleRecords);
                        transitionLOD.setUsedMinSpikedInConcentration(true);
                        transitionLOD.setTransitionID(transition);
                    }
                    
                    /*
                     * 
                     *
                     * 
                    
                    //map calibration points to records (which are repectively from the [three] replicates)
                    ExperimentCalibrationPointToRecordsMapper pointToRecordsMapper = new ExperimentCalibrationPointToRecordsMapper();
                    HashMap<String,LinkedList<PeptideRecord>> caliPointToRecords = 
                            pointToRecordsMapper.mapCalibrationPointsToRecords(transitionRecords);
                    
                    //map replicates (curve) to serial dilution records
                    ExperimentReplicateToRecordsMapper expReplicateToRecords = new ExperimentReplicateToRecordsMapper();
                    HashMap<String,LinkedList<PeptideRecord>> replicateToRecords = 
                            expReplicateToRecords.mapReplicatesToRecords(transitionRecords);

                    * 
                    */
                    lods.add(transitionLOD);
                }
                break;
            
            case SUMMED:
                // uses all records mapped to peptide irrespective of  
                //getBlanks before the first curve
                logWriter.println("    summed-transition(s) associated LOD estimation...");
                int preCurveBlanks = Integer.parseInt(config.get("preCurveBlanks"));
                LinkedList<PeptideRecord> preFirstCurveBlanks = getPreFirstCurveBlanks(peptideRecords, preCurveBlanks);
                logWriter.println("      " + preFirstCurveBlanks.size() + " pre-first curve blanks");
                LimitOfDetection summedLOD;
                LinkedList<PeptideRecord> summedTransitionsDerivedPeptideRecords = 
                        sumEachReplicateRunTransitions(preFirstCurveBlanks, 
                                                           PeptideRecordsType.PRECURVEBLANKS, 
                                                               pointToDilutionMap, 
                                                                   config,
                                                                       logWriter);
                summedLOD = computeLOD(summedTransitionsDerivedPeptideRecords);
                // update attributes
                summedLOD.setUsedMinSpikedInConcentration(false);
                summedLOD.setTransitionID("SUMMED");

                // however, if no detectable signal is observed in blanks... 
                if(summedLOD.getLimitOfDetection() <= 0){
                    //use standard deviation of signal observed/detected in lowest spiked sample 
                    LinkedList<PeptideRecord> lowestSpikedSampleRecords = getLowestSpikedSampleRecords(peptideRecords);
                    summedTransitionsDerivedPeptideRecords = 
                        sumEachReplicateRunTransitions(lowestSpikedSampleRecords, 
                                                        PeptideRecordsType.MINSPIKEDCONCENTRATION, 
                                                            pointToDilutionMap, 
                                                                config,
                                                                    logWriter);
                    summedLOD = computeLOD(summedTransitionsDerivedPeptideRecords);
                    summedLOD.setUsedMinSpikedInConcentration(true);
                    summedLOD.setTransitionID("SUMMED");
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
                // estimate summed lod...
                LinkedList<LimitOfDetection> summedLODs = 
                        estimateLOD(peptideRecords, 
                                        PeptideResultOutputType.SUMMED, 
                                            pointToDilutionMap, 
                                                config, logWriter);
                // add summed-lod to lods 
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
        LimitOfDetection lod = null;
        //determined from blanks injected before first curve using average plus 3x standard deviation of blank signal
        double[] values = new double[preFirstCurveBlanks.size()];
        for(int i = 0; i < values.length; i++){
            values[i] = preFirstCurveBlanks.get(i).getMeasuredConcentration();
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

    private LinkedList<PeptideRecord> getLowestSpikedSampleRecords(LinkedList<PeptideRecord> transitionRecords) {
        LinkedList<PeptideRecord> lowestSpikedSampleRecords = new LinkedList<PeptideRecord>();
        //get the lowest spikedIn Value
        for(PeptideRecord record: transitionRecords){
            if(record.getCalibrationPoint().equalsIgnoreCase("point_1")){
                lowestSpikedSampleRecords.add(record);
            }
        }
        return lowestSpikedSampleRecords;
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
                                                                        PrintWriter logWriter) throws FileNotFoundException, IOException {
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
                
            case MINSPIKEDCONCENTRATION:
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
                    replicateSummedPeptideRecord.setDilution(pointToDilutionMap.get("Point_1")); //minimum_spikedIn concentration.
                    replicateSummedPeptideRecord.setCalibrationPoint("Point_1");
                    //...
                    summedReplicateRunTransitions.add(replicateSummedPeptideRecord);
                }  
                break;           
        }
        //HashMap<String
        return summedReplicateRunTransitions;
    }

    
    
}
