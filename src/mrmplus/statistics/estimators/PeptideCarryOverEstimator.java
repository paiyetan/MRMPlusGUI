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
import mrmplus.statistics.resultobjects.CarryOver;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

/**
 *
 * @author paiyeta1
 */
public class PeptideCarryOverEstimator {

    public LinkedList<CarryOver> estimateCarryOver(LinkedList<PeptideRecord> sequenceMappedPeptideRecords, 
                                                        PeptideResultOutputType peptideResultOutputType, 
                                                            HashMap<String, Double> pointToDilutionMap, 
                                                                HashMap<String, String> config, 
                                                                    PrintWriter logWriter) {
        //throw new UnsupportedOperationException("Not yet implemented");
        LinkedList<CarryOver> carryOver = new LinkedList<CarryOver>();
        switch (peptideResultOutputType){
            
            case TRANSITIONS:
                //estimate LOD per transition...
                logWriter.println("    transition(s) associated LLOQ estimation...");
                System.out.println("    transition(s) associated LLOQ estimation...");
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
                    // retrieve blank peptide records after the after the highest dilution concentration
                    // NB: This is hard coded as peptide records with run order 11, 20, and 29
                    LinkedList<PeptideRecord> carryOverBlankRecords = new LinkedList<PeptideRecord>();
                    for(PeptideRecord transitionRecord : transitionRecords){
                        if(transitionRecord.getRunOrder().equalsIgnoreCase("11") || 
                                transitionRecord.getRunOrder().equalsIgnoreCase("20") ||
                                    transitionRecord.getRunOrder().equalsIgnoreCase("29")){
                            carryOverBlankRecords.add(transitionRecord);
                        }
                    }
                    logWriter.println("      " + carryOverBlankRecords.size() + " post-max blank peptide records found...");
                    System.out.println("      " + carryOverBlankRecords.size() + " post-max blank peptide records found...");
                    // find the average of the peak area of these summedMaxConcRecords...
                    double postMaxBlankPeakArea = computePeakAreaAverage(carryOverBlankRecords);
                    
                    // retieve peptide records with the highest concentration i.e. those at maximum calibration point...
                    // NB: this is also hard coded into the program..
                    LinkedList<PeptideRecord> maxConcRecords = new LinkedList<PeptideRecord>();
                    for(PeptideRecord transitionRecord : transitionRecords){
                        if(transitionRecord.getCalibrationPoint().equalsIgnoreCase("Point_7")){
                            maxConcRecords.add(transitionRecord);
                        }
                    }
                    logWriter.println("      " + maxConcRecords.size() + " maximum concentration peptide records found...");
                    System.out.println("      " + maxConcRecords.size() + " maximum concentration peptide records found...");
                    // find the average of the peak area of these summedMaxConcRecords...
                    double maxConcPointPeakArea = computePeakAreaAverage(maxConcRecords);
                    // add to LinkedList of CarryOver objects...           
                    carryOver.add(new CarryOver(transition, postMaxBlankPeakArea, maxConcPointPeakArea));
                }
                break;
            
            case SUMMED:
                // uses all records mapped to peptide irrespective of transition  
                logWriter.println("    computing summed carry over...");
                System.out.println("    computing summed carry over...");
        
                // extract and compute summedMaxConcRecords...
                LinkedList<PeptideRecord> summedCarryOverBlankRecords = 
                        extractSummedCarryOverBlankRecords(sequenceMappedPeptideRecords, logWriter);
                double summedPostMaxBlankRecordsPeakArea = computePeakAreaAverage(summedCarryOverBlankRecords);
                
                // extract and compute summedMaxConcRecords...
                LinkedList<PeptideRecord> summedMaxConcRecords = 
                        extractSummedMaxConcRecords(sequenceMappedPeptideRecords, logWriter);
                double summedMaxConcRecordsPeakArea = computePeakAreaAverage(summedMaxConcRecords);
                
                carryOver.add(new CarryOver("SUMMED", summedPostMaxBlankRecordsPeakArea, summedMaxConcRecordsPeakArea));
                
                break;
            
            default: // BOTH
                // estimate LOD per transition...
                LinkedList<CarryOver> transitionsCarryOvers = 
                        estimateCarryOver(sequenceMappedPeptideRecords, 
                                        PeptideResultOutputType.TRANSITIONS, 
                                            pointToDilutionMap, 
                                                config, logWriter);
                // add estimated transition-lloqs to lloqs
                for(CarryOver transitionsCarryOver : transitionsCarryOvers){
                    carryOver.add(transitionsCarryOver);
                }                
                // estimate summed lloq...
                LinkedList<CarryOver> summedCarryOvers = 
                        estimateCarryOver(sequenceMappedPeptideRecords, 
                                        PeptideResultOutputType.SUMMED, 
                                            pointToDilutionMap, 
                                                config, logWriter);
                // add summed-lloq to lloqs 
                for(CarryOver summedCarryOver : summedCarryOvers){
                    carryOver.add(summedCarryOver);
                }
            
        }
                       
        return carryOver;
    }

    private double computePeakAreaAverage(LinkedList<PeptideRecord> carryOverBlankRecords) {
        //throw new UnsupportedOperationException("Not yet implemented");
        double average = 0;
        double[] values = new double[carryOverBlankRecords.size()];
        for(int i = 0; i < carryOverBlankRecords.size(); i++){
            values[i] = carryOverBlankRecords.get(i).getLightArea();  //NB. we used the light peak area....                      
        }
        //compute average
        Mean mean = new Mean();
        average = mean.evaluate(values);
        return average;
    }

    private LinkedList<PeptideRecord> getRunOrderRecords(LinkedList<PeptideRecord> sequenceMappedPeptideRecords, String runOrder) {
        //throw new UnsupportedOperationException("Not yet implemented");
        LinkedList<PeptideRecord> peptideRecords = new LinkedList<PeptideRecord>();
        for(PeptideRecord peptideRecord : sequenceMappedPeptideRecords){
            if(peptideRecord.getRunOrder().equalsIgnoreCase(runOrder)){
                peptideRecords.add(peptideRecord);
            }
        }       
        return peptideRecords;
        
    }

    private LinkedList<PeptideRecord> extractSummedCarryOverBlankRecords(LinkedList<PeptideRecord> sequenceMappedPeptideRecords,
                                                                            PrintWriter logWriter) {
        LinkedList<PeptideRecord> summedCarryOverBlankRecords = new LinkedList<PeptideRecord>();
        PeptideRecordsSummer summer = new PeptideRecordsSummer();
                
        // for all sequenceMappedPeptideRecords, get the 11, 20, and 29 run_order peptideRecords;
        // 11th run order peptide records
        LinkedList<PeptideRecord> eleventhRunOrders = getRunOrderRecords(sequenceMappedPeptideRecords, "11");
        logWriter.println("     " + eleventhRunOrders.size() + " eleventh run order elements associated with peptide...");
        System.out.println("     " + eleventhRunOrders.size() + " eleventh run order elements associated with peptide...");
        // expectedly there should be three eleventh run order objects corresponding to the respective transitions;
        // sum these records
        PeptideRecord summedEleventhPeptideRecord = summer.sumPeptideRecords(eleventhRunOrders);
        summedCarryOverBlankRecords.add(summedEleventhPeptideRecord);

        // 20th run order peptide records
        LinkedList<PeptideRecord> twentiethRunOrders = getRunOrderRecords(sequenceMappedPeptideRecords, "20");
        logWriter.println("     " + twentiethRunOrders.size() + " twentieth run order elements associated with peptide...");
        System.out.println("     " + twentiethRunOrders.size() + " twentieth run order elements associated with peptide...");
        // expectedly there should be three eleventh run order objects corresponding to the respective transitions;
        // sum these records
        PeptideRecord summedtwentiethPeptideRecord = summer.sumPeptideRecords(twentiethRunOrders);
        summedCarryOverBlankRecords.add(summedtwentiethPeptideRecord);
                
        // 29th run order peptide records
        LinkedList<PeptideRecord> twentyNinethRunOrders = getRunOrderRecords(sequenceMappedPeptideRecords, "29");
        logWriter.println("     " + twentyNinethRunOrders.size() + " twentyNineth run order elements associated with peptide...");
        System.out.println("     " + twentyNinethRunOrders.size() + " twentyNineth run order elements associated with peptide...");
        // expectedly there should be three eleventh run order objects corresponding to the respective transitions;
        // sum these records
        PeptideRecord summedtwentyNinethPeptideRecord = summer.sumPeptideRecords(twentyNinethRunOrders);
        summedCarryOverBlankRecords.add(summedtwentyNinethPeptideRecord);
        
        return summedCarryOverBlankRecords;
    }

    private LinkedList<PeptideRecord> extractSummedMaxConcRecords(LinkedList<PeptideRecord> sequenceMappedPeptideRecords, 
                                                            PrintWriter logWriter) {
        LinkedList<PeptideRecord> summedMaxConcRecords = new LinkedList<PeptideRecord>();
        PeptideRecordsSummer summer = new PeptideRecordsSummer();
                
        // for all sequenceMappedPeptideRecords, get the 10, 19, and 28 run_order peptideRecords;
        // 11th run order peptide records
        LinkedList<PeptideRecord> tenthRunOrders = getRunOrderRecords(sequenceMappedPeptideRecords, "10");
        logWriter.println("     " + tenthRunOrders.size() + " tenth run order elements associated with peptide...");
        System.out.println("     " + tenthRunOrders.size() + " tenth run order elements associated with peptide...");
        // expectedly there should be three eleventh run order objects corresponding to the respective transitions;
        // sum these records
        PeptideRecord summedtenthPeptideRecord = summer.sumPeptideRecords(tenthRunOrders);
        summedMaxConcRecords.add(summedtenthPeptideRecord);

        // 20th run order peptide records
        LinkedList<PeptideRecord> nineteenthRunOrders = getRunOrderRecords(sequenceMappedPeptideRecords, "19");
        logWriter.println("     " + nineteenthRunOrders.size() + " nineteenth run order elements associated with peptide...");
        System.out.println("     " + nineteenthRunOrders.size() + " nineteenth run order elements associated with peptide...");
        // expectedly there should be three eleventh run order objects corresponding to the respective transitions;
        // sum these records
        PeptideRecord summednineteenthPeptideRecord = summer.sumPeptideRecords(nineteenthRunOrders);
        summedMaxConcRecords.add(summednineteenthPeptideRecord);
                
        // 29th run order peptide records
        LinkedList<PeptideRecord> twentyEighthRunOrders = getRunOrderRecords(sequenceMappedPeptideRecords, "28");
        logWriter.println("     " + twentyEighthRunOrders.size() + " twentyEighth run order elements associated with peptide...");
        System.out.println("     " + twentyEighthRunOrders.size() + " twentyEighth run order elements associated with peptide...");
        // expectedly there should be three eleventh run order objects corresponding to the respective transitions;
        // sum these records
        PeptideRecord summedtwentyEighthPeptideRecord = summer.sumPeptideRecords(twentyEighthRunOrders);
        summedMaxConcRecords.add(summedtwentyEighthPeptideRecord);
        
        return summedMaxConcRecords;
    }
    
    
    
}
