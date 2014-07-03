/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.estimators;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import mrmplus.PeptideRecord;
import mrmplus.PeptideTransitionToRecordsMapper;
import mrmplus.enums.PeptideResultOutputType;
import mrmplus.statistics.resultobjects.Linearity;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 *
 * @author paiyeta1
 */
public class PeptideLinearityEstimator {

    public LinkedList<Linearity> estimateLinearity(LinkedList<PeptideRecord> peptideRecords, 
                                            PeptideResultOutputType peptideResultOutputType, 
                                                HashMap<String, Double> pointToDilutionMap, 
                                                    HashMap<String, String> config, 
                                                        PrintWriter logWriter) {
        LinkedList<Linearity> lEstimates = new LinkedList<Linearity>();
        
        switch (peptideResultOutputType){
            
            case TRANSITIONS:
                //estimate LOD per transition...
                logWriter.println("    transition(s) associated Linearity estimation...");
                System.out.println("    transition(s) associated Linearity estimation...");
                PeptideTransitionToRecordsMapper transToRecordsMapper = new PeptideTransitionToRecordsMapper();
                HashMap<String, LinkedList<PeptideRecord>> transitionToRecords = 
                        transToRecordsMapper.mapTransitionsToRecords(peptideRecords);
                Set<String> transitions = transitionToRecords.keySet();
                logWriter.println("    " + transitions.size() + " transitions found associated with peptide " + 
                        peptideRecords.getFirst().getPeptideSequence());
                System.out.println("    " + transitions.size() + " transitions found associated with peptide " + 
                        peptideRecords.getFirst().getPeptideSequence());
                for(String transition : transitions){
                    // for each transition,
                    //  get records associated with said transition....
                    LinkedList<PeptideRecord> transitionRecords = transitionToRecords.get(transition);
                    logWriter.println("     to transition " + transition + ", " + transitionRecords.size() + 
                                                              " peptide records were mapped...");
                    System.out.println("     to transition " + transition + ", " + transitionRecords.size() + 
                                                              " peptide records were mapped...");
                    //
                    // N.B. For each transition, there are a number of conc. measures (caliberation pointa) for which 
                    // three replicates were performed at each point...
                    // map calibration points to records (which are repectively from the [three] replicates)
                    ExperimentCalibrationPointToRecordsMapper pointToRecordsMapper = 
                            new ExperimentCalibrationPointToRecordsMapper();
                    HashMap<String,LinkedList<PeptideRecord>> caliPointToRecords = 
                            pointToRecordsMapper.mapCalibrationPointsToRecords(transitionRecords);
                    logWriter.println("     to transition " + transition + ", " + caliPointToRecords.keySet().size() + 
                                                              " calibration points were found...");
                    System.out.println("     to transition " + transition + ", " + caliPointToRecords.keySet().size() + 
                                                              " calibration points were found...");
                    
                    // compute linearity for transition from values associated with calibration point
                    Linearity linearity = computeLinearity(transition, caliPointToRecords, pointToDilutionMap);                    
                    lEstimates.add(linearity);
                }
                break;
            
            case SUMMED:
                // uses all records mapped to peptide irrespective of transition  
                // Group records by concentration point (s)
                logWriter.println("    computing summed transition(s) associated Linearity estimation...");
                System.out.println("    computing summed transition(s) associated Linearity estimation...");
                ExperimentCalibrationPointToRecordsMapper pointToRecordsMapper = 
                            new ExperimentCalibrationPointToRecordsMapper();
                logWriter.println("    mapping records to respective calibration point...");
                System.out.println("    mapping records to respective calibration point...");
                HashMap<String,LinkedList<PeptideRecord>> caliPointToRecords = 
                        pointToRecordsMapper.mapCalibrationPointsToRecords(peptideRecords);
                logWriter.println("     "  + caliPointToRecords.keySet().size() + " calibration point(s) found: ");
                System.out.println("     "  + caliPointToRecords.keySet().size() + " calibration point(s) found: ");
                
                // for each concenteration point, extract a LinkedList of summedPeptideRecords of transitions 
                // for each replicate, and map this to the calibratrion point...
                HashMap<String, LinkedList<PeptideRecord>> caliPointToSummedTranRecords =
                        new HashMap<String, LinkedList<PeptideRecord>>();
                //LinkedList<CoefficientOfVariation> summedCVs = new LinkedList<CoefficientOfVariation>();
                Set<String> caliPoints = caliPointToRecords.keySet();
                for(String caliPoint : caliPoints){
                    //LinkedList<PeptideRecord> summedTransitionsRecords = new LinkedList<PeptideRecord>();
                    LinkedList<PeptideRecord> caliPointMappedRecords = caliPointToRecords.get(caliPoint);
                    logWriter.println("      "  + caliPointMappedRecords.size() + " records found mapped to " + caliPoint);
                    System.out.println("      "  + caliPointMappedRecords.size() + " records found mapped to " + caliPoint);
                    
                    // group records (calibration point mapped records) by replicate
                    logWriter.println("       mapping calibration point records to replicateId...");
                    System.out.println("       mapping calibration point records to replicateId...");
                    ExperimentReplicateToRecordsMapper rep2recMap = new ExperimentReplicateToRecordsMapper();
                    HashMap<String, LinkedList<PeptideRecord>> replicate2RecordsMap = 
                            rep2recMap.mapReplicatesToRecords(caliPointMappedRecords);
                    // expectedly at this stage each replicate record should contain only values from point's associated transitions
                    // sum the values in each replicate (for associated transitions) into a value
                    LinkedList<PeptideRecord> summedTransitionsRecords = new LinkedList<PeptideRecord>();
                    Set<String> replicates = replicate2RecordsMap.keySet();
                    for(String replicate : replicates){
                        LinkedList<PeptideRecord> replicateRecords = replicate2RecordsMap.get(replicate);
                        logWriter.println("        " + replicateRecords.size() + " records found mapped to replicateId " + 
                                replicate + " of calibration point " + caliPoint);
                        System.out.println("        " + replicateRecords.size() + " records found mapped to replicateId " + 
                                replicate + " of calibration point " + caliPoint);
                    
                        PeptideRecordsSummer prs = new PeptideRecordsSummer();
                        logWriter.println("         summing replicateId transitions..."); 
                        System.out.println("         summing replicateId transitions...");
                        PeptideRecord summedTransitionRecord = prs.sumPeptideRecords(replicateRecords);
                        summedTransitionRecord.setDilution(pointToDilutionMap.get(caliPoint));
                        summedTransitionsRecords.add(summedTransitionRecord);
                    }
                    logWriter.println("       " + summedTransitionsRecords.size() + 
                                                " summed transition records derived for " + caliPoint);
                    System.out.println("       " + summedTransitionsRecords.size() + 
                                                " summed transition records derived for " + caliPoint);
                    caliPointToSummedTranRecords.put(caliPoint, summedTransitionsRecords);
                    
                }
                logWriter.println("       computing linearity on summed-transition values...");
                System.out.println("       computing linearity on summed-transition values...");
                Linearity summedValueLinearity = computeLinearity("SUMMED", caliPointToSummedTranRecords, pointToDilutionMap);
                lEstimates.add(summedValueLinearity);
                
                break;
            
            default: // BOTH
                // estimate LOD per transition...
                LinkedList<Linearity> transitionsLinearities = 
                        estimateLinearity(peptideRecords, 
                                        PeptideResultOutputType.TRANSITIONS, 
                                            pointToDilutionMap, 
                                                config, logWriter);
                // add estimated transition-lloqs to lloqs
                for(Linearity transitionLinearity : transitionsLinearities){
                    lEstimates.add(transitionLinearity);
                }                
                // estimate summed lloq...
                LinkedList<Linearity> summedLinearities = 
                        estimateLinearity(peptideRecords, 
                                        PeptideResultOutputType.SUMMED, 
                                            pointToDilutionMap, 
                                                config, logWriter);
                // add summed-lloq to lloqs 
                for(Linearity summedLinearity : summedLinearities){
                    lEstimates.add(summedLinearity);
                }
            
        }                      
        return lEstimates;
    }

    private Linearity computeLinearity(String transition, 
                                           HashMap<String, LinkedList<PeptideRecord>> caliPointToRecords,
                                                HashMap<String, Double> pointToDilutionMap) {
        Linearity linearity = null;
        String transitionID = transition;
        double slope;
        double intercept;
        double rsquared;
        double slopeStandardError;
        double midResponseValue = 0;
        double predictedMidResponseValue;
        
        //Perform a Standard linear regression...
        SimpleRegression slr = new SimpleRegression();
        Set<String> caliPoints = caliPointToRecords.keySet();
        
        //get middle calibration point;
        int midCaliPoint = caliPoints.size()/2;
        String midCaliPointID = null;
        
        int midCaliPointTracker = 0;
        for(String caliPoint : caliPoints){
            midCaliPointTracker++;
            double xCoordinate = pointToDilutionMap.get(caliPoint); // independent variable
            LinkedList<PeptideRecord> mappedRecords = caliPointToRecords.get(caliPoint);
            
            // at the middle spiked-in concentration...
            if(midCaliPointTracker == midCaliPoint){
                //get averaged measured conceteration value;
                midCaliPointID = caliPoint;
                Mean mean = new Mean();
                double[] values = new double[mappedRecords.size()];
                for(int i = 0; i < values.length; i++){
                    values[i] = mappedRecords.get(i).getMeasuredConcentration();
                }
                midResponseValue = mean.evaluate(values);
            }
            
            for(PeptideRecord mappedRecord : mappedRecords){
                double yCoordinate = mappedRecord.getMeasuredConcentration(); // response variable
                slr.addData(xCoordinate, yCoordinate);
            }
        }
        //LinkedList<Double> pairedValue;
        slope = slr.getSlope();
        intercept = slr.getIntercept();
        rsquared = slr.getRSquare();
        slopeStandardError = slr.getSlopeStdErr();
        
        // get predicted value at the middle concentration value
        double middleXCoordinate = pointToDilutionMap.get(midCaliPointID);
        predictedMidResponseValue = slr.predict(middleXCoordinate);
        
        linearity = new Linearity(transitionID, 
                                     slope, 
                                         intercept, 
                                             rsquared, 
                                                 slopeStandardError, 
                                                     midResponseValue, 
                                                         predictedMidResponseValue);       
        return linearity;
    }
       
}
