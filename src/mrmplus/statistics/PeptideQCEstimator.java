/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import mrmplus.PeptideRecord;
import mrmplus.PeptideResult;
import mrmplus.enums.PeptideResultOutputType;
import mrmplus.statistics.estimators.*;
import mrmplus.statistics.resultobjects.CarryOver;
import mrmplus.statistics.resultobjects.LimitOfDetection;
import mrmplus.statistics.resultobjects.Linearity;
import mrmplus.statistics.resultobjects.LowerLimitOfQuantification;


/**
 *
 * @author paiyeta1
 */
public class PeptideQCEstimator {
    
    
    public HashMap<String, LinkedList<PeptideResult>> estimatePeptidesQCs(HashMap<String, LinkedList<PeptideRecord>> pepToRecordsMap,
                                                                          HashMap<String, Double> pointToDilutionMap,
                                                                          HashMap<String, String> config,
                                                                          PrintWriter logWriter) throws FileNotFoundException, IOException{
        HashMap<String, LinkedList<PeptideResult>> peptideQCEstimates = new HashMap<String, LinkedList<PeptideResult>>();
        //instantiate peptide result object for each peptide and insert in peptideQCEstimates map result...
        System.out.println(" Instantiating output peptide result object(s)...");
        logWriter.println(" Instantiating output peptide result object(s)...");
        Set<String> peptideSequences = pepToRecordsMap.keySet();
        for(String peptideSequence : peptideSequences){
            peptideQCEstimates.put(peptideSequence, new LinkedList<PeptideResult>());
        }
        
        
        /*
         * QC values to estimate 
         *  double limitOfDetection;
            double lowerLimitOfQuantification;
            //Linearity linearity;
            double slope;
            double slopeStandardError;
            double slopeStandardErrorPercent;

            double carryOver; //in Percentage
            //private LinkedList<PeptideTransition> transitions; 
            double upperLimitOfQuantification;
         */
        /*
         * [default] QC values in config file
            header=TRUE
            inputFile=./etc/data/inputFile.txt
            metadataFile=./etc/data/metadata.txt
            peptidesMonitored=43
            noOftransitions=3
            preCurveBlanks=3
            totalBlanks=9
            replicates=3
            serialDilutions=7
            computeLOD=TRUE
            computeLLOQ=TRUE
            estimateLinearity=FALSE
            computeCarryOver=FALSE
            computePartialValidationOfSpecificity=FALSE
            computeULOQ=TRUE
            fitCurve=TRUE
            outputDirectory=./etc/text
            peptidesResultsOutputted=SUMMED
         * 
         */
        
        
        
        // ************************ //
        // *** Defaults to TRUE ***
        // ************************ //
        //Limit of Detection.
        if(config.get("computeLOD").equalsIgnoreCase("TRUE")){
            System.out.println(" Estimating Limit of Detection(s)...");
            logWriter.println(" Estimating Limit of Detection(s)...");
            
            PeptideLODEstimator lODEstimator = new PeptideLODEstimator();
            LinkedList<LimitOfDetection> lods = null;
            // for each of the peptide sequence
            for(String peptideSequence : peptideSequences){
                // get instantiated object [place holder for derived results]
                // the number of derived PeptideResult objects is dependent user specofied
                // peptideResulsOutputted option;
                System.out.println("  Estimating Limit of Detection(s) for peptide " + peptideSequence);
                logWriter.println("  Estimating Limit of Detection(s) for peptide "+ peptideSequence);
            
                LinkedList<PeptideResult> peptideResults = peptideQCEstimates.remove(peptideSequence);
                // get associated PeptideRecords...
                LinkedList<PeptideRecord> peptideRecords = pepToRecordsMap.get(peptideSequence);
                System.out.println("   " + peptideRecords.size() + " peptide records mapped to " + peptideSequence + " peptide...");
                logWriter.println("   " + peptideRecords.size() + " peptide records mapped to " + peptideSequence + " peptide...");
                
                // determine peptidesResultsOutputted
                if(config.get("peptidesResultsOutputted").equalsIgnoreCase("SUMMED")){
                    // compute summed LOD...
                    logWriter.println("  Estimating SUMMED associated LODs...");
                    lods = lODEstimator.estimateLOD(peptideRecords, 
                                                    PeptideResultOutputType.SUMMED, 
                                                    pointToDilutionMap, 
                                                    config,
                                                    logWriter);                
                } else if(config.get("peptidesResultsOutputted").equalsIgnoreCase("TRANSITIONS")){
                    // compute for each transitions
                    logWriter.println("  Estimating TRANSITIONS associated LODs...");
                    lods = lODEstimator.estimateLOD(peptideRecords, 
                                                    PeptideResultOutputType.TRANSITIONS, 
                                                    pointToDilutionMap, 
                                                    config,
                                                    logWriter);                    
                } else {
                    // compute for both summed and transitions...
                    logWriter.println("  Estimating BOTH (SUMMED and TRANSITIONS) associated LODs...");
                    lods = lODEstimator.estimateLOD(peptideRecords, 
                                                    PeptideResultOutputType.BOTH, 
                                                    pointToDilutionMap, 
                                                    config,
                                                    logWriter);
                }
                logWriter.println("   " + lods.size() + " LODs estimated associated with peptide '" + peptideSequence);
                
                //peptideResult.setLimitsOfDetections(lods);
                //since LOD is the first variable computed, at this stage, there should not have been any PeptideResult in PeptideSequence's 
                //respectively associated PeptideResult(s) linkedlist object.
                if(peptideResults.size()==0){
                    for(int i = 0; i < lods.size(); i++){
                        PeptideResult peptideResult = new PeptideResult(peptideSequence);
                        peptideResult.setTransitionID(lods.get(i).getTransitionID());
                        peptideResult.setLimitOfDetection(lods.get(i));
                        peptideResults.add(peptideResult);
                    }
                }else{
                    if(peptideResults.size()!=lods.size()){ // estimated lods size should be equivalent to peptide sequence associated peptideResult linkedList size
                        try{
                            throw new Exception(); 
                        }catch(Exception ex){
                            ex.printStackTrace();
                            System.out.println("ERROR: mapped peptideResults linked list size do not match computed number of LODs");
                        }
                    }else{
                        //for each lod
                        for(LimitOfDetection lod : lods){
                            //find peptideResult with same transitionID
                            String transitionID = lod.getTransitionID();
                            for(int i = 0; i < peptideResults.size(); i++){
                                if(transitionID.equalsIgnoreCase(peptideResults.get(i).getTransitionID())){
                                    peptideResults.get(i).setLimitOfDetection(lod);
                                }
                            }
                        }
                    }
                }
                //remove and re-insert peptideToPeptideResult mapping... 
                peptideQCEstimates.put(peptideSequence, peptideResults);
            }
        }
        //Lower Limit of Quantitation.
        System.out.println();
        logWriter.println();
        
        if(config.get("computeLLOQ").equalsIgnoreCase("TRUE")){
            System.out.println(" Estimating Lower Limit of Quantitation(s)...");
            logWriter.println(" Estimating Lower Limit of Quantitation(s)...");
            
            PeptideLLOQEstimator lLOQEstimator = new PeptideLLOQEstimator();
            // for each of the peptide sequence
            for(String peptideSequence : peptideSequences){
                System.out.println("  Estimating Limit of Detection(s) for peptide " + peptideSequence);
                logWriter.println("  Estimating Limit of Detection(s) for peptide "+ peptideSequence);
                
                LinkedList<PeptideResult> peptideResults = peptideQCEstimates.remove(peptideSequence);// get instantiated PeptideResult object [place holder for derived results]
                LinkedList<PeptideRecord> sequenceMappedPeptideRecords = pepToRecordsMap.get(peptideSequence); // get associated PeptideRecords...
                LinkedList<LowerLimitOfQuantification> lloqs = null;
                if(config.get("peptidesResultsOutputted").equalsIgnoreCase("SUMMED")){ // determine user specified peptidesResultsOutputted type
                    // compute summed...
                    lloqs = lLOQEstimator.estimateLLOQ(sequenceMappedPeptideRecords, 
                                                            PeptideResultOutputType.SUMMED, 
                                                                pointToDilutionMap, 
                                                                    config, 
                                                                        logWriter);                  
                } else if(config.get("peptidesResultsOutputted").equalsIgnoreCase("TRANSITIONS")){
                    // compute for each transitions
                    lloqs = lLOQEstimator.estimateLLOQ(sequenceMappedPeptideRecords, 
                                                            PeptideResultOutputType.TRANSITIONS, 
                                                                pointToDilutionMap, 
                                                                    config, 
                                                                        logWriter);   
                } else { //BOTH
                    // compute for both summed and transitions...
                    lloqs = lLOQEstimator.estimateLLOQ(sequenceMappedPeptideRecords, 
                                                            PeptideResultOutputType.BOTH, 
                                                                pointToDilutionMap, 
                                                                    config, 
                                                                        logWriter);  
                } 
                logWriter.println("   " + lloqs.size() + " LLOQs estimated associated with peptide '" + peptideSequence);
                // set LLOQ(s) for peptide...[in each associated peptide result object...
                // peptideResults
                for(PeptideResult peptideResult : peptideResults){
                    String transitionID = peptideResult.getTransitionID();
                    //find peptideResult with same transitionID
                    for(int i = 0; i < lloqs.size(); i++){
                        if(transitionID.equalsIgnoreCase(lloqs.get(i).getTransitionID())){
                            peptideResult.setLowerLimitOfQuantification(lloqs.get(i));
                        }
                    }
                }
                //remove and re-insert peptideToPeptideResult mapping...
                peptideQCEstimates.put(peptideSequence, peptideResults);
            }
            
        }
        
        //Estimate Linearity, similar or equivalent to fitting the curve...
        System.out.println();
        logWriter.println();
        
        if(config.get("computeLinearity").equalsIgnoreCase("TRUE")){
            System.out.println(" Estimating Linearity...");
            logWriter.println(" Estimating Linearity...");
            
            PeptideLinearityEstimator lEstimator = new PeptideLinearityEstimator();
            
            // for each of the peptide sequence
            for(String peptideSequence : peptideSequences){
                System.out.println("  Estimating Linearity(ies) for peptide " + peptideSequence);
                logWriter.println("  Estimating Linearity(ies) for peptide "+ peptideSequence);
                
                LinkedList<PeptideResult> peptideResults = peptideQCEstimates.remove(peptideSequence);// get instantiated PeptideResult object [place holder for derived results]
                LinkedList<PeptideRecord> sequenceMappedPeptideRecords = pepToRecordsMap.get(peptideSequence); // get associated PeptideRecords...
                LinkedList<Linearity> linearities = null;
                if(config.get("peptidesResultsOutputted").equalsIgnoreCase("SUMMED")){ // determine peptidesResultsOutputted
                    // compute summed...
                    linearities = lEstimator.estimateLinearity(sequenceMappedPeptideRecords, 
                                                                PeptideResultOutputType.SUMMED, 
                                                                    pointToDilutionMap, 
                                                                        config, 
                                                                            logWriter);                  
                } else if(config.get("peptidesResultsOutputted").equalsIgnoreCase("TRANSITIONS")){
                    // compute for each transitions
                    linearities = lEstimator.estimateLinearity(sequenceMappedPeptideRecords, 
                                                                PeptideResultOutputType.TRANSITIONS, 
                                                                    pointToDilutionMap, 
                                                                        config, 
                                                                            logWriter);   
                } else { //BOTH
                    // compute for both summed and transitions...
                    linearities = lEstimator.estimateLinearity(sequenceMappedPeptideRecords, 
                                                                PeptideResultOutputType.BOTH, 
                                                                    pointToDilutionMap, 
                                                                        config, 
                                                                            logWriter);  
                } 
                
                logWriter.println("   " + linearities.size() + " Linearities estimated associated with peptide '" + peptideSequence);
                // set LLOQ(s) for peptide...[in each associated peptide result object...
                // peptideResults
                for(PeptideResult peptideResult : peptideResults){
                    String transitionID = peptideResult.getTransitionID();
                    //find peptideResult with same transitionID
                    for(int i = 0; i < linearities.size(); i++){
                        if(transitionID.equalsIgnoreCase(linearities.get(i).getTransitionID())){
                            peptideResult.setLinearity(linearities.get(i));
                        }
                    }
                }
                //remove and re-insert peptideToPeptideResult mapping...
                peptideQCEstimates.put(peptideSequence, peptideResults);
            }
        }
        
        // ************************* //
        // *** Defaults to FALSE ***
        // ************************* //
        //Estimate Upper Limit of Quantitation.
        if(config.get("computeULOQ").equalsIgnoreCase("TRUE")){
            System.out.println(" Estimating Upper Limit of Quantitation...");
            logWriter.println(" Estimating Upper Limit of Quantitation...");
            
            PeptideULOQEstimator uLOQEstimator = new PeptideULOQEstimator();
            
            // for each of the peptide sequence
            for(String peptideSequence : peptideSequences){
                // get instantiated PeptideResult object [place holder for derived results]
                LinkedList<PeptideResult> peptideResults = peptideQCEstimates.get(peptideSequence);
                // get associated PeptideRecords...
                LinkedList<PeptideRecord> peptideRecords = pepToRecordsMap.get(peptideSequence);
                
                
                // determine peptidesResultsOutputted
                if(config.get("peptidesResultsOutputted").equalsIgnoreCase("SUMMED")){
                    // compute summed...
                    
                } else if(config.get("peptidesResultsOutputted").equalsIgnoreCase("TRANSITIONS")){
                    // compute for each transitions
                    
                } else {
                    // compute for both summed and transitions...
                }
                
                //set ULOQ(s) for peptide...
                
                //remove and re-insert peptideToPeptideResult mapping... 
            }
            
        }
        
        
        //Estimate Carry-Over.
        /*
         * peak area of analyte peptide in blank after the highest concentration
         * divided by peak area in high concentration sample and expressed as percent;
         * three blanks, one after each high concentration are averaged.
         * 
         */
        if(config.get("computeCarryOver").equalsIgnoreCase("TRUE")){
            System.out.println(" Computing carry-over...");
            logWriter.println(" Computing carry-over...");
            
            PeptideCarryOverEstimator cOEstimator = new PeptideCarryOverEstimator();
            //System.out.println(" Estimating Lower Limit of Quantitation(s)...");
            //logWriter.println(" Estimating Lower Limit of Quantitation(s)...");
            
            //PeptideLLOQEstimator lLOQEstimator = new PeptideLLOQEstimator();
            // for each of the peptide sequence
            // for each of the peptide sequence
            for(String peptideSequence : peptideSequences){
                System.out.println("  Computing carry-over for peptide " + peptideSequence);
                logWriter.println("  Computing carry-over for peptide "+ peptideSequence);
                
                LinkedList<PeptideResult> peptideResults = peptideQCEstimates.remove(peptideSequence);// get instantiated PeptideResult object [place holder for derived results]
                LinkedList<PeptideRecord> sequenceMappedPeptideRecords = pepToRecordsMap.get(peptideSequence); // get associated PeptideRecords...
                LinkedList<CarryOver> carryOvers = null;
                if(config.get("peptidesResultsOutputted").equalsIgnoreCase("SUMMED")){ // determine user specified peptidesResultsOutputted type
                    // compute summed...
                    carryOvers = cOEstimator.estimateCarryOver(sequenceMappedPeptideRecords, 
                                                            PeptideResultOutputType.SUMMED, 
                                                                pointToDilutionMap, 
                                                                    config, 
                                                                        logWriter);                  
                } else if(config.get("peptidesResultsOutputted").equalsIgnoreCase("TRANSITIONS")){
                    // compute for each transitions
                    carryOvers = cOEstimator.estimateCarryOver(sequenceMappedPeptideRecords, 
                                                            PeptideResultOutputType.TRANSITIONS, 
                                                                pointToDilutionMap, 
                                                                    config, 
                                                                        logWriter);   
                } else { //BOTH
                    // compute for both summed and transitions...
                    carryOvers = cOEstimator.estimateCarryOver(sequenceMappedPeptideRecords, 
                                                            PeptideResultOutputType.BOTH, 
                                                                pointToDilutionMap, 
                                                                    config, 
                                                                        logWriter);  
                } 
                logWriter.println("   " + carryOvers.size() + " CarryOver objects associated with peptide '" + peptideSequence);
                // set LLOQ(s) for peptide...[in each associated peptide result object...
                // peptideResults
                for(PeptideResult peptideResult : peptideResults){
                    String transitionID = peptideResult.getTransitionID();
                    //find peptideResult with same transitionID
                    for(int i = 0; i < carryOvers.size(); i++){
                        if(transitionID.equalsIgnoreCase(carryOvers.get(i).getTransitionID())){
                            peptideResult.setCarryOver(carryOvers.get(i));
                        }
                    }
                }
                //remove and re-insert peptideToPeptideResult mapping...
                peptideQCEstimates.put(peptideSequence, peptideResults);
            }          
        }
        //Partially validate Specificity.
        if(config.get("computePartialValidationOfSpecificity").equalsIgnoreCase("TRUE")){
            System.out.println(" Partially validating specificity...");
            logWriter.println(" Partially validating specificity...");
            
            PeptideSpecValidator specValidator = new PeptideSpecValidator();
            
            // for each of the peptide sequence
            for(String peptideSequence : peptideSequences){
                // get instantiated PeptideResult object [place holder for derived results]
                LinkedList<PeptideResult> peptideResults = peptideQCEstimates.get(peptideSequence);
                // get associated PeptideRecords...
                LinkedList<PeptideRecord> peptideRecords = pepToRecordsMap.get(peptideSequence);
                
                
                // determine peptidesResultsOutputted
                if(config.get("peptidesResultsOutputted").equalsIgnoreCase("SUMMED")){
                    // compute summed...
                    
                } else if(config.get("peptidesResultsOutputted").equalsIgnoreCase("TRANSITIONS")){
                    // compute for each transitions
                    
                } else {
                    // compute for both summed and transitions...
                }
                
                //set PartialSpecValidationValues(s) for peptide...
                
                //remove and re-insert peptideToPeptideResult mapping... 
            }
        }
        
        return peptideQCEstimates;
    }

    
    
}
