/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.estimators;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import mrmplus.PeptideRecord;
import mrmplus.PeptideTransitionToRecordsMapper;
import mrmplus.enums.PeptideResultOutputType;
import mrmplus.statistics.resultobjects.CoefficientOfVariation;
import mrmplus.statistics.resultobjects.LowerLimitOfQuantification;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

/**
 *
 * @author paiyeta1
 */
public class PeptideLLOQEstimator {
    
    public LinkedList<LowerLimitOfQuantification> estimateLLOQ(
                                                    //LinkedList<PeptideResult> peptideResults,
                                                    LinkedList<PeptideRecord> peptideRecords, 
                                                    PeptideResultOutputType peptideResultOutputType,
                                                    HashMap<String, Double> pointToDilutionMap,
                                                    HashMap<String, String> config,
                                                    PrintWriter logWriter) throws FileNotFoundException, IOException {
        //throw new UnsupportedOperationException("Not yet implemented");
        LinkedList<LowerLimitOfQuantification> lloqs = new LinkedList<LowerLimitOfQuantification>();
        switch (peptideResultOutputType){
            
            case TRANSITIONS:
                //estimate LOD per transition...
                logWriter.println("    transition(s) associated LLOQ estimation...");
                PeptideTransitionToRecordsMapper transToRecordsMapper = new PeptideTransitionToRecordsMapper();
                HashMap<String, LinkedList<PeptideRecord>> transitionToRecords = 
                        transToRecordsMapper.mapTransitionsToRecords(peptideRecords);
                Set<String> transitions = transitionToRecords.keySet();
                logWriter.println("    " + transitions.size() + " transitions found associated with peptide " + 
                        peptideRecords.getFirst().getPeptideSequence());
                for(String transition : transitions){
                    // for each transition,
                    //  get records associated with said transition....
                    LinkedList<PeptideRecord> transitionRecords = transitionToRecords.get(transition);
                    logWriter.println("     to transition " + transition + ", " + transitionRecords.size() + 
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
                    //for each of the calibration point, compute CVs
                    LinkedList<CoefficientOfVariation> transitonCVs = new LinkedList<CoefficientOfVariation>();
                    logWriter.println("       values found calibration points were: ");
                    Set<String> caliPoints = caliPointToRecords.keySet();
                    for(String caliPoint : caliPoints){
                        logWriter.print("        " + caliPoint + ": ");
                        LinkedList<PeptideRecord> mappedRecords = caliPointToRecords.get(caliPoint);
                        double[] values = new double[mappedRecords.size()];
                        for(int i = 0; i < mappedRecords.size(); i++){
                            values[i] = mappedRecords.get(i).getMeasuredConcentration();
                            if(i == mappedRecords.size()-1)
                                logWriter.print(values[i] + "\n");
                            else 
                                logWriter.print(values[i] + ", ");
                        }
                        //compute average
                        Mean mean = new Mean();
                        double average = mean.evaluate(values);
                        logWriter.println("          Mean: " + average);
                        //compute standard_deviation
                        StandardDeviation sdev = new StandardDeviation();
                        double sd = sdev.evaluate(values);
                        logWriter.println("          St_Dev: " + sd);
                        //compute cv
                        double cv = (sd/average) * 100;
                        logWriter.println("          Coef_Var: " + cv);
                        CoefficientOfVariation coef = new CoefficientOfVariation(caliPoint, average, sd, cv);
                        transitonCVs.add(coef);
                    }
                    
                    //compute LLOQ
                    CoefficientOfVariation lloq = computeLLOQ(transitonCVs, pointToDilutionMap);
                    
                    /*
                    //map replicates (curve) to serial dilution records
                    ExperimentReplicateToRecordsMapper expReplicateToRecords = new ExperimentReplicateToRecordsMapper();
                    HashMap<String,LinkedList<PeptideRecord>> replicateToRecords = 
                            expReplicateToRecords.mapReplicatesToRecords(transitionRecords);

                    * 
                    */
                    lloqs.add(new LowerLimitOfQuantification(transition, lloq));
                }
                break;
            
            case SUMMED:
                // uses all records mapped to peptide irrespective of transition  
                // Group records by concentration point (s)
                ExperimentCalibrationPointToRecordsMapper pointToRecordsMapper = 
                            new ExperimentCalibrationPointToRecordsMapper();
                HashMap<String,LinkedList<PeptideRecord>> caliPointToRecords = 
                        pointToRecordsMapper.mapCalibrationPointsToRecords(peptideRecords);
                // for each concenteration point, compute cv,
                LinkedList<CoefficientOfVariation> summedCVs = new LinkedList<CoefficientOfVariation>();
                Set<String> caliPoints = caliPointToRecords.keySet();
                for(String caliPoint : caliPoints){
                    LinkedList<PeptideRecord> caliPointMappedRecords = caliPointToRecords.get(caliPoint);
                    // group records (calibration point mapped records) by replicate
                    ExperimentReplicateToRecordsMapper rep2recMap = new ExperimentReplicateToRecordsMapper();
                    HashMap<String, LinkedList<PeptideRecord>> replicate2RecordsMap = 
                            rep2recMap.mapReplicatesToRecords(caliPointMappedRecords);
                    // expectedly at this stage each replicate record should contain only values from point's associated transitions
                    // sum the values in each replicate (for associated transitions) into a value
                    LinkedList<PeptideRecord> summedTransitionsRecords = new LinkedList<PeptideRecord>();
                    Set<String> replicates = replicate2RecordsMap.keySet();
                    for(String replicate : replicates){
                        LinkedList<PeptideRecord> replicateRecords = replicate2RecordsMap.get(replicate);
                        PeptideRecordsSummer prs = new PeptideRecordsSummer();
                        PeptideRecord summedTransitionRecord = prs.sumPeptideRecords(replicateRecords);
                        summedTransitionsRecords.add(summedTransitionRecord);
                    }
                    // compute cv on the
                    double[] values = new double[summedTransitionsRecords.size()];
                    for(int i = 0; i < summedTransitionsRecords.size(); i++){
                        values[i] = summedTransitionsRecords.get(i).getMeasuredConcentration();                        
                    }
                    //compute average
                    Mean mean = new Mean();
                    double average = mean.evaluate(values);
                    //logWriter.println("          Mean: " + average);
                    //compute standard_deviation
                    StandardDeviation sdev = new StandardDeviation();
                    double sd = sdev.evaluate(values);
                    //logWriter.println("          St_Dev: " + sd);
                    //compute cv
                    double cv = (sd/average) * 100;
                    //logWriter.println("          Coef_Var: " + cv);
                    CoefficientOfVariation coef = new CoefficientOfVariation(caliPoint, average, sd, cv);
                    summedCVs.add(coef);
                }
                
                //compute LLOQ
                CoefficientOfVariation lloq = computeLLOQ(summedCVs, pointToDilutionMap);
                lloqs.add(new LowerLimitOfQuantification("SUMMED", lloq));
                
                break;
            
            default: // BOTH
                // estimate LOD per transition...
                LinkedList<LowerLimitOfQuantification> transitionsLLOQs = 
                        estimateLLOQ(peptideRecords, 
                                        PeptideResultOutputType.TRANSITIONS, 
                                            pointToDilutionMap, 
                                                config, logWriter);
                // add estimated transition-lloqs to lloqs
                for(LowerLimitOfQuantification transitionLLOQ : transitionsLLOQs){
                    lloqs.add(transitionLLOQ);
                }                
                // estimate summed lloq...
                LinkedList<LowerLimitOfQuantification> summedLLOQs = 
                        estimateLLOQ(peptideRecords, 
                                        PeptideResultOutputType.SUMMED, 
                                            pointToDilutionMap, 
                                                config, logWriter);
                // add summed-lloq to lloqs 
                for(LowerLimitOfQuantification sumLLOQ : summedLLOQs){
                    lloqs.add(sumLLOQ);
                }
            
        }
                       
        return lloqs;
    }

    
    private CoefficientOfVariation computeLLOQ(LinkedList<CoefficientOfVariation> coeffs, 
                                                    HashMap<String, Double> pointToDilutionMap) {
        CoefficientOfVariation lloq = null;
        // get coeffs with < 20 cv
        LinkedList<CoefficientOfVariation> coeffsBelow20 = new LinkedList<CoefficientOfVariation> ();
        for(CoefficientOfVariation coeff : coeffs){
            if(coeff.getCoef() < 20){
                coeffsBelow20.add(coeff);
            }
        }
        // get that with the lowest concenteration
        lloq = coeffsBelow20.getFirst();
        double lowestDilution = pointToDilutionMap.get(lloq.getId());
        for(CoefficientOfVariation coeffBelow20 : coeffsBelow20){
            double dilution = pointToDilutionMap.get(coeffBelow20.getId());
            if(dilution < lowestDilution){
                lloq = coeffBelow20;
            }
        }        
        return lloq;
    }   
    
}
