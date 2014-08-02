/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics;

import ios.Logger;
import ios.RepeatabilityInputFileReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import mrmplus.PeptideRecord;
import mrmplus.enums.PeptideResultOutputType;
import mrmplus.statistics.estimators.ExpIICoefficientOfVariationsEstimator;
import mrmplus.statistics.mappers.PeptideToRecordsMapper;
import mrmplus.statistics.resultobjects.experimentii.ExpIIMiniValidationOfRepeatabilityResult;
import mrmplus.statistics.resultobjects.experimentii.ExpIIValidationIdAndConcLevelsCoefs;

/**
 *
 * @author paiyeta1
 */
public class PeptideRepeatabilityMiniValidator {

    public void validateRepeatability(HashMap<String, String> config) {
            // instantiate a Logger
            long rpt_start_time = new Date().getTime();
            String logFile = "." + File.separator + "logs" + File.separator + rpt_start_time + ".repeatability.log";
            Logger logger = new Logger(logFile, true);
            
        try {
                        
            // read preprocessedRepeatability input file...
            String rptInputFile = config.get("preprocessedRepeatabilityData");
            RepeatabilityInputFileReader reader = new RepeatabilityInputFileReader();
            LinkedList<PeptideRecord> peptideRecords = reader.readInputFile(rptInputFile, config, logger);
            
            // map peptide sequence to associated records
            PeptideToRecordsMapper pepseqToRecordsMapper = new PeptideToRecordsMapper();
            HashMap<String, LinkedList<PeptideRecord>> sequenceToMappedPeptideRecords = 
                    pepseqToRecordsMapper.mapPeptideToRecord(peptideRecords);
            
            //HashMap<String, LinkedList<PeptideRepeatabilityValidationResult>> peptideRepeatabilityValidations = 
            //        new HashMap<String, LinkedList<PeptideRepeatabilityValidationResult>>();
            
            Set<String> peptideSequences = sequenceToMappedPeptideRecords.keySet();
            
            //instantiate peptide validation result object for each peptide and insert in peptideRepeatabilityValidations map result...
            logger.println(" Instantiating output peptide result object(s)...");
            /*
             * today@07312014, decided to store the result in a map "peptideRepeatabilityValidationResultMap"
               the peptideRepeatabilityValidationResultMap maps to each peptide, a ExpIIMiniValidationOfRepeatabilityResult derived
               result - this is an object which holds the peptide sequence and a map of transition(s) or summed-transition(s) 
               mapped to respectively associated ExpIIValidationIdAndConcLevelsCoefs (concetration level estimated coefficients)
            
               other experiment II associated computations ((i) validated LLOQ, and (ii) partial validation of 
               specificity are performed with the result object. 
            * 
            */
            HashMap<String, ExpIIMiniValidationOfRepeatabilityResult> peptideRepeatabilityValidationResultMap = 
                    new HashMap<String, ExpIIMiniValidationOfRepeatabilityResult>();
            
            //For Each Peptide Sequence and its mapped peptide records
            for(String peptideSequence : peptideSequences){
                /*
                 * Depending on the peptide output result type desired, there should be a number of 
                 * PeptideRepeatabilityValidation result objects associated with each peptide sequence.
                 * If PeptideResultOutputType.SUMMED, there would be just one which is derived from summing transition associated
                 * values; if PeptideResultOutputType.TRANSITIONS, there would as much as there are transitions, i.e. one for 
                 * each transition; if PeptideResultOutputType.BOTH, there would a sum of the above number 
                 * PeptideRepeatabilityValidationResult objects.  
                 * 
                 */
                // get sequence mapped peptide records...
                LinkedList<PeptideRecord> sequenceMappedRecords = sequenceToMappedPeptideRecords.get(peptideSequence);
                
                // ******************************************************************************** //
                //              Estimate Coefficient of variation(s) associated values              //
                // ******************************************************************************** //
                // instantiate per concentration level CV estimator...
                ExpIICoefficientOfVariationsEstimator eIICVsEstimator = new ExpIICoefficientOfVariationsEstimator();
                
                HashMap<String, ExpIIValidationIdAndConcLevelsCoefs> subIdsToConcLevelsCoefsMap = null;
                    // sub-ids to concentration level(s) associated coefficient of variations...
                    // NOTE: sub-ids could be one of the many transitions monitored for a particular peptide...(See above),
                    // if if PeptideResultOutputType == TRANSITIONS, subIds will represent each transition, if 
                    // PeptideResultOutputType == BOTH, subIds will represent each transition and a "SUMMED" identifier, and if 
                    // PeptideResultOutputType == SUMMED, subIds will represent only the SUMMED identifier.
                               
                // determine PeptideResultOutputType 
                if(config.get("PeptideResultOutputType").equalsIgnoreCase("TRANSITION")){
                    // get respective transition(s) to respectively associated concentration level Coefficients
                    subIdsToConcLevelsCoefsMap = eIICVsEstimator.estimateCoefficients(sequenceMappedRecords,
                                                                        PeptideResultOutputType.TRANSITIONS,
                                                                            config,
                                                                                logger);                   
                } else if(config.get("PeptideResultOutputType").equalsIgnoreCase("SUMMED")){
                    // get summedTransitions to associated concentration level Coefficients
                    subIdsToConcLevelsCoefsMap = eIICVsEstimator.estimateCoefficients(sequenceMappedRecords,
                                                                        PeptideResultOutputType.SUMMED,
                                                                            config,
                                                                                logger);                    
                } else { //BOTH
                    // get respective transition(s) AND summedTransitions value (per experiment) 
                    // to respectively associated concentration level Coefficients
                    subIdsToConcLevelsCoefsMap = eIICVsEstimator.estimateCoefficients(sequenceMappedRecords,
                                                                        PeptideResultOutputType.BOTH,
                                                                            config,
                                                                                logger);                                  
                }
                
                // intantiate an ExpIIMiniValidationOfRepeatabilityResult object;
                ExpIIMiniValidationOfRepeatabilityResult miniValidationOfRepeatabilityResult = 
                       new ExpIIMiniValidationOfRepeatabilityResult(peptideSequence); 
                miniValidationOfRepeatabilityResult.setSubIdsToConcLevelsCoefficientsMap(subIdsToConcLevelsCoefsMap);
                peptideRepeatabilityValidationResultMap.put(peptideSequence, miniValidationOfRepeatabilityResult);
            
            }
            
            
            //print output results...
            
            
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            java.util.logging.Logger.getLogger(PeptideRepeatabilityMiniValidator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            ex.printStackTrace();
            java.util.logging.Logger.getLogger(PeptideRepeatabilityMiniValidator.class.getName()).log(Level.SEVERE, null, ex);
        }      
        
    }
    
    
    
    
}