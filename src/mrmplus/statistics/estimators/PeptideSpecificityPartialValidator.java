/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.estimators;

import mrmplus.statistics.mappers.ExpICalibrationPointToRecordsMapper;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import mrmplus.PeptideRecord;
import mrmplus.PeptideResult;
import mrmplus.statistics.mappers.PeptideTransitionToRecordsMapper;
import mrmplus.enums.PeptideResultOutputType;
import mrmplus.statistics.resultobjects.CoefficientOfVariation;
import mrmplus.statistics.resultobjects.LowerLimitOfQuantification;
import mrmplus.statistics.resultobjects.PartialValidation;
import mrmplus.statistics.resultobjects.PartialValidationOfSpecificity;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.rank.Max;

/**
 *
 * @author paiyeta1
 *
 * Described as the ratio of transitions determined in each sample at all concentrations (ratios 
 * of peak areas of different transitions of the same peptide).
 * In all samples above the LLOQ, no transition ratio should deviate >30% from the mean
 * 
 */
public class PeptideSpecificityPartialValidator {

    public LinkedList<PartialValidationOfSpecificity> 
            partiallyValidateSpecificity(LinkedList<PeptideRecord> sequenceMappedPeptideRecords, 
                                            PeptideResultOutputType peptideResultOutputType, 
                                                HashMap<String, Double> pointToDilutionMap, 
                                                    HashMap<String, String> config, 
                                                        PrintWriter logWriter,
                                                            LinkedList<PeptideResult> peptideResults
                                         ) {
        
        
        //throw new UnsupportedOperationException("Not yet implemented");
        LinkedList<PartialValidationOfSpecificity> pvspecificities = new LinkedList<PartialValidationOfSpecificity>();
        
        switch (peptideResultOutputType){
            
            case TRANSITIONS:
                // estimate partial validation of specificity per transition...
                
                /*
                 * the ratio of transitions determined in each sample at all concentrations (ratios 
 * of peak areas of different transitions of the same peptide).
 * In all samples above the LLOQ, no transition ratio should deviate >30% from the mean
                 */
                
                logWriter.println("    transition(s) associated partial validation of specificity estimation...");
                System.out.println("    transition(s) associated partial validation of specificity estimation...");
                PeptideTransitionToRecordsMapper transToRecordsMapper = new PeptideTransitionToRecordsMapper();
                HashMap<String, LinkedList<PeptideRecord>> transitionToRecords = 
                        transToRecordsMapper.mapTransitionsToRecords(sequenceMappedPeptideRecords); //Each peptide associated transition is mapped to 
                                                                                                    //  peptide records which 
                                                                                                    //  includes records at the varying
                                                                                                    //  calibration points; with the replicates
                                                                                                    //  per calibration point                
                Set<String> transitions = transitionToRecords.keySet();
                logWriter.println("    " + transitions.size() + " transitions found associated with peptide " + 
                        sequenceMappedPeptideRecords.getFirst().getPeptideSequence());
                System.out.println("    " + transitions.size() + " transitions found associated with peptide " + 
                        sequenceMappedPeptideRecords.getFirst().getPeptideSequence());
                for(String transition : transitions){
                    //  for each transition,
                    //  get records associated with said transition....
                    LinkedList<PeptideRecord> transitionRecords = transitionToRecords.get(transition);
                    logWriter.println("     to transition " + transition + ", " + transitionRecords.size() + 
                                                              " peptide records were mapped...");
                    System.out.println("     to transition " + transition + ", " + transitionRecords.size() + 
                                                              " peptide records were mapped...");
                    //
                    // N.B. For each transition, there are a number of conc. measures (caliberation points) for which 
                    // three replicates were performed at each point...
                    // map calibration points to records (which are repectively from the [three] replicates)
                    ExpICalibrationPointToRecordsMapper pointToRecordsMapper = 
                            new ExpICalibrationPointToRecordsMapper();
                    HashMap<String,LinkedList<PeptideRecord>> caliPointToRecords = 
                            pointToRecordsMapper.mapCalibrationPointsToRecords(transitionRecords);
                    
                    // At this point, determine the calibration for which the lower limit of quantification was found
                    String lLOQCali_Point = getTransitionLLOQCalibrationPoint(transition, peptideResults);
                    logWriter.println("     to transition " + transition + ", LLOQ calibration point was " + lLOQCali_Point);
                    System.out.println("     to transition " + transition + ", LLOQ calibration point was " + lLOQCali_Point);
                    // recall, for partial validation of specificity; 
                    //         in all samples above the LLOQ, no transition ratio should deviate >30% from the mean
                    
                    //Therefore,
                    // iterate through all calibration points, 
                    //      if calibration point is > llOQ calibration point, 
                    //          instantiate a PartialValidation object
                    // get calibration_point with maximum deviation and instantiate a partial validation of specificity object
                  
                    logWriter.println("      estimating calibration point above LLOQ with maximum deviation");
                    System.out.println("      estimating calibration point above LLOQ with maximum deviation");
                    
                    LinkedList<PartialValidation> caliPointValidation = new LinkedList<PartialValidation>();
                    // down-stream computation are only meaningful if there was quantifiable LLOQ, in which case the 
                    //   returned lLOQCali_Point !equalIgnoreCase "NA".
                    if(lLOQCali_Point.equalsIgnoreCase("NA")==false){
                        // for each of the calibration point
                        Set<String> caliPoints = caliPointToRecords.keySet();
                        for(String caliPoint : caliPoints){
                            if(compareCalibrationPoints(caliPoint, lLOQCali_Point) >= 1){
                                LinkedList<PeptideRecord> caliPointMappedRecords = caliPointToRecords.get(caliPoint); // these should correspond to replicate records

                                //get peak area ratios of calibration point mapped records
                                double[] values = new double[caliPointMappedRecords.size()];
                                for(int i = 0; i < caliPointMappedRecords.size(); i++){
                                    values[i] = caliPointMappedRecords.get(i).getPeakAreaRatio();

                                }
                                //get mean peak area ratio
                                Mean mean = new Mean();
                                double caliPointAverage = mean.evaluate(values);                        
                                //get deviation from mean peak area ratio for each peak area ratio
                                double[] deviations = new double[values.length];
                                for(int i = 0; i < values.length; i++){
                                    double transitionReplicatePeakArea = values[i];
                                    deviations[i] = 
                                            (Math.abs(caliPointAverage - transitionReplicatePeakArea)/caliPointAverage) * 100;
                                }
                                // get maximum deviation
                                Max max = new Max();
                                double caliPointMaxDeviation = max.evaluate(deviations);
                                // instantiate a PartialValidation object
                                caliPointValidation.add(new PartialValidation(transition, caliPoint, 
                                                                        caliPointAverage, caliPointMaxDeviation));

                            }                       
                        }
                    }
                    if(caliPointValidation.size() > 0){
                        //get PartialValidation object with maximum deviation
                        PartialValidation pValidation = partialValidationWithMaxDeviation(caliPointValidation);
                        pvspecificities.add(new PartialValidationOfSpecificity(transition, 
                                                                                true,
                                                                                pValidation.getCaliPointMaxDeviation(),
                                                                                pValidation.getCaliPointAverage(),
                                                                                pValidation.getCalibrationPoint()));
                    } else if(caliPointValidation.size() == 0){ //in which case no calibration point has values over the LLOQ cali_Point
                        pvspecificities.add(new PartialValidationOfSpecificity(transition, 
                                                                                false,
                                                                                0.0,0.0,"NA"));
                    }
                             
                }
                break;
            
            case SUMMED:
                //SUMMED attributes does not apply to Partial Validation of Specificaity  
                pvspecificities.add(new PartialValidationOfSpecificity("SUMMED", false,
                                                                                0.0,0.0,"NA"));
                
                break;
            
            default: // BOTH
                // estimate pvspecificities per transition...
                LinkedList<PartialValidationOfSpecificity> transitionsPVSpecificities = 
                        partiallyValidateSpecificity(sequenceMappedPeptideRecords, 
                                        PeptideResultOutputType.TRANSITIONS, 
                                            pointToDilutionMap, 
                                                config, logWriter, peptideResults);
                // add estimated transition-pvspecificities to pvspecificities
                for(PartialValidationOfSpecificity transitionPVSpecificity : transitionsPVSpecificities){
                    pvspecificities.add(transitionPVSpecificity);
                }                
                // estimate summed pvspecificities...
                LinkedList<PartialValidationOfSpecificity> summedPVSpecificities = 
                        partiallyValidateSpecificity(sequenceMappedPeptideRecords, 
                                        PeptideResultOutputType.SUMMED, 
                                            pointToDilutionMap, 
                                                config, logWriter, peptideResults);
                // add summed-lloq to lloqs 
                for(PartialValidationOfSpecificity summedPVSpecificity : summedPVSpecificities){
                    pvspecificities.add(summedPVSpecificity);
                }
            
        }
                       
        return pvspecificities;
    }

    private String getTransitionLLOQCalibrationPoint(String transition, LinkedList<PeptideResult> peptideResults) {
        //throw new UnsupportedOperationException("Not yet implemented");
        String cali_point = null;
        for(PeptideResult peptideResult : peptideResults){
            if(peptideResult.getTransitionID().equalsIgnoreCase(transition)){
                LowerLimitOfQuantification lLOQ = peptideResult.getLowerLimitOfQuantification();
                CoefficientOfVariation cv = lLOQ.getCoefficientOfVariation();
                //for an assay with no quantifiable CV below specified cutoff, cv == null, therefore check this to prevent a nullPointer
                if(cv == null){
                    cali_point = "NA"; //place holder for non-applicable computation.
                }else{
                    cali_point = cv.getCalibrationPoint();
                }
            }
        }        
        return cali_point;
    }

    private int compareCalibrationPoints(String caliPoint, String lLOQCali_Point) {
        //throw new UnsupportedOperationException("Not yet implemented");
        int compare = 0;
        int caliPointIndex = Integer.parseInt(caliPoint.split("_")[1]);
        int lLOQCaliPointIndex = Integer.parseInt(lLOQCali_Point.split("_")[1]);
        compare = Integer.valueOf(caliPointIndex).compareTo(Integer.valueOf(lLOQCaliPointIndex));
        return compare;
    }

    private PartialValidation partialValidationWithMaxDeviation(LinkedList<PartialValidation> caliPointValidation) {
        //throw new UnsupportedOperationException("Not yet implemented");
        PartialValidation pValidation = caliPointValidation.getFirst();
        for(PartialValidation cpValidation : caliPointValidation){
            if(cpValidation.getCaliPointMaxDeviation() > pValidation.getCaliPointMaxDeviation()){
                pValidation = cpValidation;
            }
        }
        return pValidation;
    }
    
}
