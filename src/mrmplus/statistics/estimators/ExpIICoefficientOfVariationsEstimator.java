/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.estimators;

import ios.Logger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import mrmplus.PeptideRecord;
import mrmplus.enums.PeptideResultOutputType;
import mrmplus.statistics.mappers.ExpIIConcLevelToRecordsMapper;
import mrmplus.statistics.mappers.PeptideTransitionToRecordsMapper;
import mrmplus.statistics.resultobjects.experimentii.ExpIIConcLevelCoefficientOfVariation;
import mrmplus.statistics.resultobjects.experimentii.ExpIIValidationIdAndConcLevelsCoefs;

/**
 *
 * @author paiyeta1
 */
public class ExpIICoefficientOfVariationsEstimator {
    
    public HashMap<String, ExpIIValidationIdAndConcLevelsCoefs> estimateCoefficients(LinkedList<PeptideRecord> sequenceMappedRecords, 
                                                                        PeptideResultOutputType peptideResultOutputType, 
                                                                            HashMap<String, String> config, 
                                                                                Logger logger) {
        logger.println("      Estimating Coefficients...");
        HashMap<String, ExpIIValidationIdAndConcLevelsCoefs> idToConcLevelsCoefsMap = 
                new HashMap<String, ExpIIValidationIdAndConcLevelsCoefs>();
        
        //determine output type and solve approriately...
        switch(peptideResultOutputType) {
            
            case TRANSITIONS:
                logger.println("       Estimating TRANSITION(S) associated Coefficients...");
                PeptideTransitionToRecordsMapper ptrMapper = new PeptideTransitionToRecordsMapper();
                logger.println("        Mapping transition(s) to records...");
                HashMap<String, LinkedList<PeptideRecord>> transitionToRecordsMap = 
                        ptrMapper.mapTransitionsToRecords(sequenceMappedRecords);
                logger.println("         " + transitionToRecordsMap.size() + " transition(s) to records map found");
                //for each transition and its associated records, map concentrationLevelToPeptideRecords
                Set<String> transitionIds = transitionToRecordsMap.keySet();
                
                //++++++++++++++++++++++++++++++++++++++++++++++++++//
                // output transitions found..
                logger.print("          transitions found: ");
                for(String transitionId : transitionIds){
                    logger.print(transitionId + " ");
                }
                logger.print("\n");   
                //++++++++++++++++++++++++++++++++++++++++++++++++++//
                
                for(String transitionId : transitionIds){
                    logger.println("           For transiton " + transitionId);
                    // get transition mapped peptide records
                    LinkedList<PeptideRecord> tMappedPeptideRecords = 
                            transitionToRecordsMap.get(transitionId);
                    logger.println("             " + tMappedPeptideRecords.size() + " peptide records found mapped");
                    // derive concentration level associated coefficient of variations...
                    logger.println("             Estimating concentration level associated coeficients...");
                    // but first, map records to available concentration Levels
                    logger.println("               Mapping peptide records to associated concentration level of transiton " + transitionId);
                    ExpIIConcLevelToRecordsMapper expIIConcLevelToRecordsMapper = new ExpIIConcLevelToRecordsMapper();
                    HashMap<String, LinkedList<PeptideRecord>> expIIConcLevelToRecordsMap =
                            expIIConcLevelToRecordsMapper.mapExperimentConcLevelToRecords(tMappedPeptideRecords);
                    Set<String> concLevels = expIIConcLevelToRecordsMap.keySet();
                    logger.println("               " + concLevels.size() + " concentration levels found...");
                    
                    // +++++++++
                    // output concentration levels found...
                    logger.print("                 Concentration levels: ");
                    for(String concLevel : concLevels){
                        logger.print(concLevel + " ");
                    }
                    logger.print("\n");                   
                    // +++++++++
                    
                    logger.println("               Mapping peptide records of transiton " + transitionId + 
                            " to associated concentration level(s)...");
                    
                    // map concetrationLevel to associated CV object...
                    HashMap<String, ExpIIConcLevelCoefficientOfVariation> concLevelToCoeffficientsMap = 
                            new HashMap<String, ExpIIConcLevelCoefficientOfVariation>();
                    for(String concLevel : concLevels){
                        logger.println("                 For concentration Level: " + concLevel);
                        // get transition associated concentration level mapped peptide records...
                        LinkedList<PeptideRecord> tConcLevelMappedRecords = expIIConcLevelToRecordsMap.get(concLevel);
                        logger.println("                  " + tConcLevelMappedRecords.size() + " peptide records were found...");
                        
                        logger.println("                   Estimating concentration level associated coefficients...");
                        ExpIIConcLevelCoefficientOfVariation expIIConcLevelCV = 
                                new ExpIIConcLevelCoefficientOfVariation(transitionId, concLevel,
                                                                            tConcLevelMappedRecords, //could be individual transition or summed
                                                                                config,
                                                                                    logger);
                        logger.println("                   Mapping concentration level coefficient estimates to concentration level...");
                        concLevelToCoeffficientsMap.put(concLevel, expIIConcLevelCV);                       
                    }
                    // create miniValidationOfRepeatability object
                    logger.println("               Instantiating " + transitionId + 
                            " associated ExpIIValidationIdAndConcLevelsCoefs (transitionId, concLevelToCoeffficientsMap)...");
                    ExpIIValidationIdAndConcLevelsCoefs miniValidation = 
                            new ExpIIValidationIdAndConcLevelsCoefs(transitionId, concLevelToCoeffficientsMap);
                    // map derived concentration level associated cvs to id (in this case transition...
                    logger.println("               Mapping transiton " + transitionId + 
                            " to associated ExpIIValidationIdAndConcLevelsCoefs...");
                    idToConcLevelsCoefsMap.put(transitionId, miniValidation);                                 
                }                
                break;  
                
            case SUMMED:
                logger.println("       Estimating SUMMED-TRANSITION(S) values associated Coefficients...");
                PeptideRecordsSummer prs = new PeptideRecordsSummer();
                
                logger.println("       Summing transition associated records into a record...");
                LinkedList<PeptideRecord> summedtransitionPeptideRecords = 
                        prs.sumPeptideRecords(sequenceMappedRecords, "SUMMED", logger);
                logger.println("         " + summedtransitionPeptideRecords.size() + " summed-transition(s) record values derived...");
                
                
                    
                // derive concentration level associated coefficient of variations...
                logger.println("         Estimating concentration level associated coeficients...");
                 // but first, map records to available concentration Levels
                logger.println("         Mapping summed records to associated concentration level");
                ExpIIConcLevelToRecordsMapper expIIConcLevelToRecordsMapper = new ExpIIConcLevelToRecordsMapper();
                HashMap<String, LinkedList<PeptideRecord>> expIIConcLevelToRecordsMap =
                        expIIConcLevelToRecordsMapper.mapExperimentConcLevelToRecords(summedtransitionPeptideRecords);
                Set<String> concLevels = expIIConcLevelToRecordsMap.keySet();
                
                logger.println("         " + concLevels.size() + " concentration levels found...");
                // +++++++++
                // output concentration levels found...
                logger.print("           Concentration levels: ");
                for(String concLevel : concLevels){
                    logger.print(concLevel + " ");
                }
                logger.print("\n");                   
                // +++++++++
                // map concetrationLevel to associated CV object...
                HashMap<String, ExpIIConcLevelCoefficientOfVariation> concLevelToCoeffficientsMap = 
                        new HashMap<String, ExpIIConcLevelCoefficientOfVariation>();
                for(String concLevel : concLevels){
                    logger.println("           For concentration Level: " + concLevel);
                    // get transition associated concentration level mapped peptide records...
                    LinkedList<PeptideRecord> tConcLevelMappedRecords = expIIConcLevelToRecordsMap.get(concLevel);
                    logger.println("             " + tConcLevelMappedRecords.size() + " peptide records were found...");
                        
                    logger.println("             Estimating concentration level associated coefficients...");
                    ExpIIConcLevelCoefficientOfVariation expIIConcLevelCV = 
                            new ExpIIConcLevelCoefficientOfVariation("SUMMED", concLevel,
                                                                        tConcLevelMappedRecords, //could be individual transition or summed
                                                                            config,
                                                                                logger);
                    logger.println("             Mapping concentration level coefficient estimates to concentration level...");
                    concLevelToCoeffficientsMap.put(concLevel, expIIConcLevelCV);                       
                }
                // create miniValidationOfRepeatability object
                logger.println("             Instantiating " + "SUMMED" + 
                            " associated ExpIIValidationIdAndConcLevelsCoefs (SUMMED, concLevelToCoeffficientsMap)...");
                ExpIIValidationIdAndConcLevelsCoefs miniValidation = 
                        new ExpIIValidationIdAndConcLevelsCoefs("SUMMED", concLevelToCoeffficientsMap);
                // map derived concentration level associated cvs to id (in this case summed-transition...
                logger.println("             Mapping transiton " +  "SUMMED" + 
                            " to associated ExpIIValidationIdAndConcLevelsCoefs...");
                idToConcLevelsCoefsMap.put("SUMMED", miniValidation);
                
                break;
                
            default: //BOTH
                // get transition associated computation of idToConcLevelsCoefsMap
                // derive ConcLevelsCoefs per transition...
                logger.println("       Estimating BOTH SUMMED-TRANSITION(S) values and TRANSITION(S) only associated Coefficients...");
                HashMap<String, ExpIIValidationIdAndConcLevelsCoefs> transitionsIdToConcLevelsCoefsMap = 
                        estimateCoefficients(sequenceMappedRecords, 
                                                    PeptideResultOutputType.TRANSITIONS, 
                                                        config, 
                                                            logger);
                // add estimated transition-ConcLevelsCoefs to idToConcLevelsCoefsMap...
                Set<String> transitions = transitionsIdToConcLevelsCoefsMap.keySet();
                logger.println("       " + transitions.size() + 
                        " transition(s) associated transitionsIdToConcLevelsCoefsMap mappings found...");
                for(String transition : transitions){
                    logger.println("       " + transition + " mapping updated...");
                    idToConcLevelsCoefsMap.put(transition, transitionsIdToConcLevelsCoefsMap.get(transition));
                }   
                
                // estimate summed idToConcLevelsCoefsMap...
                HashMap<String, ExpIIValidationIdAndConcLevelsCoefs> summedIdToConcLevelsCoefsMap = 
                        estimateCoefficients(sequenceMappedRecords, 
                                                    PeptideResultOutputType.SUMMED, 
                                                        config, 
                                                            logger);
                // add estimated summed-ConcLevelsCoefs to idToConcLevelsCoefsMap...
                Set<String> sums = summedIdToConcLevelsCoefsMap.keySet(); // of course this should be just one element
                logger.println("       " + sums.size() + 
                        " summed-transition(s) associated summedIdToConcLevelsCoefsMap mappings found...");
                
                for(String sum : sums){
                    logger.println("       " + sum + " mapping updated...");
                    idToConcLevelsCoefsMap.put(sum, summedIdToConcLevelsCoefsMap.get(sum));
                } 
                
                break;
        }
        
        return idToConcLevelsCoefsMap;
    }
}


