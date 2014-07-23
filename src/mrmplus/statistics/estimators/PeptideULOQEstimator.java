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
import mrmplus.statistics.resultobjects.CoefficientOfVariation;
import mrmplus.statistics.resultobjects.UpperLimitOfQuantification;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

/**
 *
 * @author paiyeta1
 */
public class PeptideULOQEstimator {

    public LinkedList<UpperLimitOfQuantification> estimateULOQ(LinkedList<PeptideRecord> sequenceMappedPeptideRecords, 
                                                    PeptideResultOutputType peptideResultOutputType, 
                                                        HashMap<String, Double> pointToDilutionMap, 
                                                            HashMap<String, String> config, 
                                                                PrintWriter logWriter) {
        //throw new UnsupportedOperationException("Not yet implemented");
        LinkedList<UpperLimitOfQuantification> uloqs = new LinkedList<UpperLimitOfQuantification>();
        switch (peptideResultOutputType){
            
            case TRANSITIONS:
                //estimate LOD per transition...
                logWriter.println("    transition(s) associated ULOQ estimation...");
                System.out.println("    transition(s) associated ULOQ estimation...");
                PeptideTransitionToRecordsMapper transToRecordsMapper = new PeptideTransitionToRecordsMapper();
                HashMap<String, LinkedList<PeptideRecord>> transitionToRecords = 
                        transToRecordsMapper.mapTransitionsToRecords(sequenceMappedPeptideRecords);
                Set<String> transitions = transitionToRecords.keySet();
                logWriter.println("    " + transitions.size() + " transitions found associated with peptide " + 
                        sequenceMappedPeptideRecords.getFirst().getPeptideSequence());
                System.out.println("    " + transitions.size() + " transitions found associated with peptide " + 
                        sequenceMappedPeptideRecords.getFirst().getPeptideSequence());
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
                    //for each of the calibration point, compute CVs
                    LinkedList<CoefficientOfVariation> transitonCVs = new LinkedList<CoefficientOfVariation>();
                    logWriter.println("       values found calibration points were: ");
                    System.out.println("       values found calibration points were: ");
                    Set<String> caliPoints = caliPointToRecords.keySet();
                    for(String caliPoint : caliPoints){
                        logWriter.print("        " + caliPoint + ": ");
                        System.out.print("        " + caliPoint + ": ");
                        LinkedList<PeptideRecord> mappedRecords = caliPointToRecords.get(caliPoint);
                        double[] values = new double[mappedRecords.size()];
                        for(int i = 0; i < mappedRecords.size(); i++){
                            values[i] = mappedRecords.get(i).getMeasuredConcentration();
                            if(i == mappedRecords.size()-1){
                                logWriter.print(values[i] + "\n");
                                System.out.print(values[i] + "\n");
                            }else{ 
                                logWriter.print(values[i] + ", ");
                                System.out.print(values[i] + ", ");
                            }
                        }
                        //compute average
                        Mean mean = new Mean();
                        double average = mean.evaluate(values);
                        logWriter.println("          Mean: " + average);
                        System.out.println("          Mean: " + average);
                        //compute standard_deviation
                        StandardDeviation sdev = new StandardDeviation();
                        double sd = sdev.evaluate(values);
                        logWriter.println("          St_Dev: " + sd);
                        System.out.println("          St_Dev: " + sd);
                        //compute cv
                        double cv = (sd/average) * 100;
                        logWriter.println("          Coef_Var: " + cv);
                        System.out.println("          Coef_Var: " + cv);
                        
                        double dilutionConcentration = pointToDilutionMap.get(caliPoint);
                        // String concentrationPoint = caliPoint;
                        CoefficientOfVariation coef = new CoefficientOfVariation(average, sd, cv, 
                                                                dilutionConcentration, caliPoint);
                        // test value returned for coefficient, to ensure no Double.NaN or Double.INFINITY value is considered 
                        if(isValid(coef))
                            transitonCVs.add(coef);
                    }
                    
                    //compute LLOQ
                    CoefficientOfVariation uloq = computeULOQ(transitonCVs);
                    uloqs.add(new UpperLimitOfQuantification(transition, uloq));
                }
                break;
            
            case SUMMED:
                // uses all records mapped to peptide irrespective of transition  
                // Group records by concentration point (s)
                logWriter.println("    computing summed transition(s) associated ULOQ estimation...");
                System.out.println("    computing summed transition(s) associated ULOQ estimation...");
                ExperimentCalibrationPointToRecordsMapper pointToRecordsMapper = 
                            new ExperimentCalibrationPointToRecordsMapper();
                logWriter.println("    mapping records to respective calibration point...");
                System.out.println("    mapping records to respective calibration point...");
                HashMap<String,LinkedList<PeptideRecord>> caliPointToRecords = 
                        pointToRecordsMapper.mapCalibrationPointsToRecords(sequenceMappedPeptideRecords);
                logWriter.println("     "  + caliPointToRecords.keySet().size() + " calibration point(s) found: ");
                System.out.println("     "  + caliPointToRecords.keySet().size() + " calibration point(s) found: ");
                // for each concenteration point, compute cv,
                LinkedList<CoefficientOfVariation> summedCVs = new LinkedList<CoefficientOfVariation>();
                Set<String> caliPoints = caliPointToRecords.keySet();
                for(String caliPoint : caliPoints){
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
                    logWriter.println("       computing coefficient of variation on summed transition records derived...");
                    System.out.println("       computing coefficient of variation on summed transition records derived...");
                    // compute cv on the
                    double[] values = new double[summedTransitionsRecords.size()];
                    for(int i = 0; i < summedTransitionsRecords.size(); i++){
                        values[i] = summedTransitionsRecords.get(i).getMeasuredConcentration();                        
                    }
                    //compute average
                    Mean mean = new Mean();
                    double average = mean.evaluate(values);
                    logWriter.println("        Mean: " + average);
                    System.out.println("        Mean: " + average);
                    //compute standard_deviation
                    StandardDeviation sdev = new StandardDeviation();
                    double sd = sdev.evaluate(values);
                    logWriter.println("        St_Dev: " + sd);
                    System.out.println("        St_Dev: " + sd);
                    //compute cv
                    double cv = (sd/average) * 100;
                    logWriter.println("        Coef_Var: " + cv);
                    System.out.println("        Coef_Var: " + cv);
                    double dilutionConcentration = pointToDilutionMap.get(caliPoint);
                    CoefficientOfVariation coef = new CoefficientOfVariation(average, sd, cv, 
                                                            dilutionConcentration, caliPoint);
                    
                    // test value returned for coefficient, to ensure no Double.NaN or Double.INFINITY value is considered 
                    if(isValid(coef))
                        summedCVs.add(coef);
                }
                
                //compute LLOQ
                logWriter.println("    computing summed transition(s) associated LLOQ from coefficient of variations...");
                System.out.println("    computing summed transition(s) associated LLOQ from coefficient of variations...");
                CoefficientOfVariation uloq = computeULOQ(summedCVs);
                uloqs.add(new UpperLimitOfQuantification("SUMMED", uloq));
                
                break;
            
            default: // BOTH
                // estimate LOD per transition...
                LinkedList<UpperLimitOfQuantification> transitionsULOQs = 
                        estimateULOQ(sequenceMappedPeptideRecords, 
                                        PeptideResultOutputType.TRANSITIONS, 
                                            pointToDilutionMap, 
                                                config, logWriter);
                // add estimated transition-uloqs to uloqs
                for(UpperLimitOfQuantification transitionULOQ : transitionsULOQs){
                    uloqs.add(transitionULOQ);
                }                
                // estimate summed uloq...
                LinkedList<UpperLimitOfQuantification> summedULOQs = 
                        estimateULOQ(sequenceMappedPeptideRecords, 
                                        PeptideResultOutputType.SUMMED, 
                                            pointToDilutionMap, 
                                                config, logWriter);
                // add summed-uloq to uloqs 
                for(UpperLimitOfQuantification sumULOQ : summedULOQs){
                    uloqs.add(sumULOQ);
                }
            
        }
                       
        return uloqs;
    }

    
    private CoefficientOfVariation computeULOQ(LinkedList<CoefficientOfVariation> coeffs) {
        CoefficientOfVariation uloq = null;
        if(coeffs.size() > 0){
            // get coeffs with < 20 cv
            LinkedList<CoefficientOfVariation> coeffsBelow20 = new LinkedList<CoefficientOfVariation> ();
            for(CoefficientOfVariation coeff : coeffs){
                //test coef..
                if(isValid(coeff)){
                    if(coeff.getCoef() < 20){
                        coeffsBelow20.add(coeff);
                    }
                }
            }
            // get that with the highest concenteration
            if(coeffsBelow20.size() > 0){
                uloq = coeffsBelow20.getFirst(); 

                // an alternate measure - the averaged measured concenteration value at respective spiked-in concenteration  
                double highestAveragedConc = uloq.getMean(); 
                for(CoefficientOfVariation coeffBelow20 : coeffsBelow20){
                    double averagedConc = coeffBelow20.getMean();                    
                    if(averagedConc > highestAveragedConc){
                        uloq = coeffBelow20;
                    }
                }
            }
        }
        return uloq;
    }

    /*
     * Test if the coefficient of variation attribute of object is a valid numeral i.e.
     * not Double.NaN not Double.POSITIVE_INFINITY or Double.NEGATIVE_INFINITY
     * 
     * @param coef
     * @return valid
     */
    private boolean isValid(CoefficientOfVariation coef) {
        //throw new UnsupportedOperationException("Not yet implemented");
        boolean valid = true;
        double coefficient = coef.getCoef();
        if(Double.isNaN(coefficient) || Double.isInfinite(coefficient))
            valid = false;       
        return valid;
    }
    
}
