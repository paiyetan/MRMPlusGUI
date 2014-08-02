/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.resultobjects.experimentii;

import ios.Logger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import mrmplus.PeptideRecord;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.rank.Max;

/**
 *
 * @author paiyeta1
 * @date 20140731
 * 
 * 
 * 
 */
public class ExpIIMiniValidationOfRepeatabilityResult {
    
    
    private String peptideSequence;
    private HashMap<String, ExpIIValidationIdAndConcLevelsCoefs> subIdsToConcLevelsCoefsMap;
    /*
     * other experiment II associated computations 
            (i) validated LLOQ for each subId(i.e transition or summed-transition), and 
           (ii) partial validation of specificity for each subId(i.e transition or summed-transition)
       are performed within this result object since these are somewhat dependent on previously
       estimated values. 
     * 
     */
    private HashMap<String, ExpIIPeptideValidatedLLOQ> subIdsToValidatedLLOQMap; // i.e transition to validatedLLOQ object
    private HashMap<String, ExpIIPeptidePartialValidationOfSpecificity>  subIdsToPartialValidationOfSpecMap; // i.e transition to PartialValidationOfSpecificity object
    
    public ExpIIMiniValidationOfRepeatabilityResult(String peptideSequence){
        this.peptideSequence = peptideSequence;
    }
    
    public ExpIIMiniValidationOfRepeatabilityResult(String peptideSequence,
                                HashMap<String, ExpIIValidationIdAndConcLevelsCoefs> subIdsToConcLevelsCoefsMap){
        this.peptideSequence = peptideSequence;
        this.subIdsToConcLevelsCoefsMap = subIdsToConcLevelsCoefsMap;
        setValidatedLLOQMap();
        setPartialValidationOfSpecificityMap();
    }

    public void setSubIdsToConcLevelsCoefficientsMap(HashMap<String, ExpIIValidationIdAndConcLevelsCoefs> subIdsToConcLevelsCoefsMap) {
        this.subIdsToConcLevelsCoefsMap = subIdsToConcLevelsCoefsMap;
    }

    /*
     * Lowest of three concentrations at which total estimated variability is < 20%
     */
    private void setValidatedLLOQMap() {
        
        subIdsToValidatedLLOQMap = new HashMap<String, ExpIIPeptideValidatedLLOQ>();
        
        //throw new UnsupportedOperationException("Not yet implemented");
        Set<String> subIds = subIdsToConcLevelsCoefsMap.keySet();
        //for subId, get ExpIIValidationIdAndConcLevelsCoefs
        for(String subId : subIds){
            
            ExpIIPeptideValidatedLLOQ subIdValidatedLLOQ = null;
            //extract/get associated concLevelToCoeffficientsMap
            ExpIIValidationIdAndConcLevelsCoefs valIdNConcLevelCoefs =  subIdsToConcLevelsCoefsMap.get(subId);
            HashMap<String, ExpIIConcLevelCoefficientOfVariation> concLevelToCoeffficientsMap = 
                    valIdNConcLevelCoefs.getConcLevelToCoeffficientsMap();
            
            Set<String> concLevels = concLevelToCoeffficientsMap.keySet();
            ExpIIConcLevelCoefficientOfVariation lowestTotalCV = null;
            //get the ExpIIConcLevelCoefficientOfVariation with the lowest totalCV    
            //for each transition/subId associated/mapped concLevel, get the mapped ExpIIConcLevelCoefficientOfVariation object,
            for(String concLevel : concLevels){
                ExpIIConcLevelCoefficientOfVariation concLevelCVsObject = concLevelToCoeffficientsMap.get(concLevel);
                if(lowestTotalCV==null){
                    lowestTotalCV = concLevelCVsObject;
                }else if(concLevelCVsObject.getTotalCV() < lowestTotalCV.getTotalCV()){
                    lowestTotalCV = concLevelCVsObject;
                }
            }
            if(lowestTotalCV!=null){
                String concentrationLevel = lowestTotalCV.getConcentrationLevel();
                double totalCV = lowestTotalCV.getTotalCV();
                double intraAssayCV = lowestTotalCV.getIntraAssayCV();
                double interAssayCV = lowestTotalCV.getInterAssayCV();
                subIdValidatedLLOQ = new ExpIIPeptideValidatedLLOQ(concentrationLevel, totalCV, intraAssayCV, interAssayCV);
            } 
            subIdsToValidatedLLOQMap.put(subId, subIdValidatedLLOQ);
        }
    }

    private void setPartialValidationOfSpecificityMap() {
        //throw new UnsupportedOperationException("Not yet implemented");
        subIdsToPartialValidationOfSpecMap = new HashMap<String, ExpIIPeptidePartialValidationOfSpecificity>();
        //throw new UnsupportedOperationException("Not yet implemented");
        Set<String> subIds = subIdsToConcLevelsCoefsMap.keySet();
        //for subId, get ExpIIValidationIdAndConcLevelsCoefs
        transitions:
        for(String subId : subIds){
            
            ExpIIPeptidePartialValidationOfSpecificity subIdPartialValidation = null;
            //extract/get associated concLevelToCoeffficientsMap
            ExpIIValidationIdAndConcLevelsCoefs valIdNConcLevelCoefs =  subIdsToConcLevelsCoefsMap.get(subId);
            HashMap<String, ExpIIConcLevelCoefficientOfVariation> concLevelToCoeffficientsMap = 
                    valIdNConcLevelCoefs.getConcLevelToCoeffficientsMap();
            
            Set<String> concLevels = concLevelToCoeffficientsMap.keySet();
            //double maxDeviationValue = -1;
            //get the ExpIIConcLevelCoefficientOfVariation   
            //for each transition/subId associated/mapped concLevel, get the mapped ExpIIConcLevelCoefficientOfVariation object,
            ExpIIPartialValidationDeviation maxDeviationObject = null;
            concentrationLevel:
            for(String concLevel : concLevels){
                ExpIIConcLevelCoefficientOfVariation concLevelCVsObject = concLevelToCoeffficientsMap.get(concLevel);
                HashMap<String, LinkedList<PeptideRecord>> dayToRecordsMap = concLevelCVsObject.getDayToRecordsMap();
                Set<String> days = dayToRecordsMap.keySet();
                //get PeptideRecords for each day and
                //compute deviation
                for(String day : days){
                    LinkedList<PeptideRecord> dailyPeptideRecords = dayToRecordsMap.get(day);
                    ExpIIPartialValidationDeviation deviation = computeDeviation(dailyPeptideRecords);
                    deviation.setDay(day);
                    deviation.setConcentrationLevel(concLevel);
                    if(maxDeviationObject == null){
                        //assign the first computation of Deviation to maxDeviationObject
                        maxDeviationObject = deviation;                      
                    }else if(maxDeviationObject.getDeviation() < deviation.getDeviation()){
                        maxDeviationObject = deviation; 
                    }
                }               
            }            
            subIdPartialValidation = new ExpIIPeptidePartialValidationOfSpecificity(maxDeviationObject);
            subIdsToPartialValidationOfSpecMap.put(subId, subIdPartialValidation);           
        }               
    }

    public String getPeptideSequence() {
        return peptideSequence;
    }

    public HashMap<String, ExpIIValidationIdAndConcLevelsCoefs> getSubIdsToConcLevelsCoefsMap() {
        return subIdsToConcLevelsCoefsMap;
    }

    public HashMap<String, ExpIIPeptidePartialValidationOfSpecificity> getSubIdsToPartialValidationOfSpecMap() {
        return subIdsToPartialValidationOfSpecMap;
    }

    public HashMap<String, ExpIIPeptideValidatedLLOQ> getSubIdsToValidatedLLOQMap() {
        return subIdsToValidatedLLOQMap;
    }

    private ExpIIPartialValidationDeviation computeDeviation(LinkedList<PeptideRecord> dailyPeptideRecords) {
        // new UnsupportedOperationException("Not yet implemented");
        ExpIIPartialValidationDeviation pvd = null;
        double[] values = new double[dailyPeptideRecords.size()];
        for(int i = 0; i < values.length; i++){
            values[i] = dailyPeptideRecords.get(i).getPeakAreaRatio();
        }
        Mean mean = new Mean();
        double meanPeakArea = mean.evaluate(values);
        //get deviation from mean peak area ratio for each peak area ratio
        double[] deviations = new double[values.length];
        for(int i = 0; i < values.length; i++){
            double recordPeakArea = values[i];
            deviations[i] = (Math.abs(recordPeakArea - meanPeakArea)/meanPeakArea) * 100;
        }
        // get maximum deviation
        Max max = new Max();
        double deviation = max.evaluate(deviations);
        
        pvd = new ExpIIPartialValidationDeviation(meanPeakArea,deviation);
        return pvd;
    }
    
    

    
    
    
    
    
    
    
    
}
