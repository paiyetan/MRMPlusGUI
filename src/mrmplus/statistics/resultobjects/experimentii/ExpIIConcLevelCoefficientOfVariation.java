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
import mrmplus.statistics.mappers.ExpIIDayToRecordsMapper;
import mrmplus.statistics.mappers.ExpIIReplicateToRecordsMapper;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

/**
 *
 * @author paiyeta1
 */
public class ExpIIConcLevelCoefficientOfVariation {
    private String transitionId;
    private String concentrationLevel;
    //private LinkedList<ExpIIConcLevelCoefficientOfVariation> concLevelCVs; //concentrationLevelQCEstimation...
    
    private double intraAssayCV;
    private double interAssayCV;
    private double totalCV;
    private HashMap<String, LinkedList<PeptideRecord>> dayToRecordsMap;
    private HashMap<String, LinkedList<PeptideRecord>> replicateToRecordsMap;
    private HashMap<String, Double> dayToCVMap;
    
    public ExpIIConcLevelCoefficientOfVariation(String transitionId, 
                                                String concLevel,
                                                LinkedList<PeptideRecord> concLevelPeptideRecords, //could be individual transition or summed
                                                    HashMap<String,String> config,
                                                        Logger logger){
        this.transitionId = transitionId;
        this.concentrationLevel = concLevel;
        setDailyRecordsMap(concLevelPeptideRecords, logger);
        setDailyCoefficientOfVariationsMap(logger);
        setReplicateToRecordsMap(concLevelPeptideRecords, logger);
        computeIntraAssayCV(logger);
        computeInterAssayCV(logger);
        computeTotalAssayCV(logger);
    }
    
    private void setDailyRecordsMap(LinkedList<PeptideRecord> concLevelPeptideRecords, Logger logger) {
        logger.println("               Setting " + concentrationLevel + " of " + transitionId + " day to associated records map...");
        ExpIIDayToRecordsMapper dayToRecordsMapper = new ExpIIDayToRecordsMapper();
        dayToRecordsMap = dayToRecordsMapper.mapExperimentDayToRecords(concLevelPeptideRecords);
        logger.println("                 " + dayToRecordsMap.size() + " days found...");
        
    }

    private void setDailyCoefficientOfVariationsMap(Logger logger) {
        logger.println("               Setting " + concentrationLevel + " of " + transitionId + " day to associated CV map...");
        dayToCVMap = new HashMap<String, Double>();
        Set<String> days = dayToRecordsMap.keySet();
        logger.println("               Daily CVs: ");
        for(String day : days){
            logger.println("                Day: " + day);
            LinkedList<PeptideRecord> dayMappedRecords = dayToRecordsMap.get(day);
            logger.println("                  Number of mapped records: " + dayMappedRecords.size());
            double coef = computeCV(dayMappedRecords);
            logger.println("                  Estimated Coeficient of variation: " + coef);
            dayToCVMap.put(day, coef);
        }
    }
    
    private void setReplicateToRecordsMap(LinkedList<PeptideRecord> concLevelPeptideRecords, Logger logger) {
        logger.println("               Setting " + concentrationLevel + " of " + transitionId + " replicate to associated records map...");
        //throw new UnsupportedOperationException("Not yet implemented");
        ExpIIReplicateToRecordsMapper expIIRToRecordsMapper = new ExpIIReplicateToRecordsMapper();
        replicateToRecordsMap = expIIRToRecordsMapper.mapReplicateToRecords(concLevelPeptideRecords); 
        logger.println("                 " + replicateToRecordsMap.size() + " replicates found...");
        
    }
   
    private void computeIntraAssayCV(Logger logger) {
        // throw new UnsupportedOperationException("Not yet implemented");
        // described as average of CVs determined for each of the days.
        logger.print("               Setting " + concentrationLevel + " of " + transitionId + " intraAssay CV: ");
        Set<String> days = dayToCVMap.keySet();
        LinkedList<Double> coefs = new LinkedList<Double>();
        for(String day : days){
            coefs.add(dayToCVMap.get(day));
        }
        double[] values = new double[coefs.size()];
        for(int i = 0; i < values.length; i++){
            values[i] = coefs.get(i);
        }
        //compute average
        Mean mean = new Mean();
        double average = mean.evaluate(values);
        intraAssayCV = average;  
        logger.println(String.valueOf(intraAssayCV));
    }

    private void computeInterAssayCV(Logger logger) {
        logger.print("               Setting " + concentrationLevel + " of " + transitionId + " interAssay CV: ");
        // as described, this is calculated at each concentration by determining CV of first injection (i.e. replicate,
        // across the number of days, then the second injection, etc...
        // these CVs are averaged.
        
        Set<String> replicates = replicateToRecordsMap.keySet();
        double[] values = new double[replicates.size()];
        //for each replicateId over the number of days, compute CV of peak area ratio...
        int valueArrayIndex = 0;
        for(String replicate : replicates){
            LinkedList<PeptideRecord> replicateMappedRecords = replicateToRecordsMap.get(replicate);
            double computedCV = computeCV(replicateMappedRecords);
            values[valueArrayIndex] = computedCV;
            valueArrayIndex++;
        }
        //then, find the average of these...
        Mean mean = new Mean();
        double average = mean.evaluate(values);
        interAssayCV = average;    
        logger.println(String.valueOf(interAssayCV));
    }

    private void computeTotalAssayCV(Logger logger) {
        logger.print("               Setting " + concentrationLevel + " of " + transitionId + " totalAssay CV: ");
        //throw new UnsupportedOperationException("Not yet implemented");
        // described as the square-root of the sum of (the average intra-assay CV)^2 and 
        // (the average inter-assay CV)^2.
        // ((intraAssayCV)^2 + (interAssayCV)^2)^1/2;
        totalCV = Math.sqrt((Math.pow(intraAssayCV, 2) + Math.pow(interAssayCV, 2)));
        logger.println(String.valueOf(totalCV));
    }
    
    
    private double computeCV(LinkedList<PeptideRecord> mappedRecords) {
        //throw new UnsupportedOperationException("Not yet implemented");
        double[] values = new double[mappedRecords.size()];
        for(int i = 0; i < values.length; i++){
            values[i] = mappedRecords.get(i).getPeakAreaRatio();
        }
        //compute average
        Mean mean = new Mean();
        double average = mean.evaluate(values);
        
        //compute standard_deviation
        StandardDeviation sdev = new StandardDeviation();
        double sd = sdev.evaluate(values);
        
        //compute cv
        double coef = (sd/average) * 100;
        
        return coef;
    }

    public double getInterAssayCV() {
        return interAssayCV;
    }

    public double getIntraAssayCV() {
        return intraAssayCV;
    }

    public double getTotalCV() {
        return totalCV;
    }

    public String getConcentrationLevel() {
        return concentrationLevel;
    }

    public String getTransitionId() {
        return transitionId;
    }

    public HashMap<String, Double> getDayToCVMap() {
        return dayToCVMap;
    }

    public HashMap<String, LinkedList<PeptideRecord>> getDayToRecordsMap() {
        return dayToRecordsMap;
    }

    public HashMap<String, LinkedList<PeptideRecord>> getReplicateToRecordsMap() {
        return replicateToRecordsMap;
    }

    
    

   
    
}
