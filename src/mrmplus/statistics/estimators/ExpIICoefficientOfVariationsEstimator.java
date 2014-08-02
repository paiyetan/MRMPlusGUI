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
        HashMap<String, ExpIIValidationIdAndConcLevelsCoefs> idToConcLevelsCoefsMap = 
                new HashMap<String, ExpIIValidationIdAndConcLevelsCoefs>();
        
        //determine output type and solve approriately...
        switch(peptideResultOutputType) {
            
            case TRANSITIONS:
                PeptideTransitionToRecordsMapper ptrMapper = new PeptideTransitionToRecordsMapper();
                HashMap<String, LinkedList<PeptideRecord>> transitionToRecordsMap = 
                        ptrMapper.mapTransitionsToRecords(sequenceMappedRecords);
                //for each transition and its associated records, map concentrationLevelToPeptideRecords
                Set<String> transitionIds = transitionToRecordsMap.keySet();
                for(String transitionId : transitionIds){
                    // get transition mapped peptide records
                    LinkedList<PeptideRecord> tMappedPeptideRecords = 
                            transitionToRecordsMap.get(transitionId);
                    // derive concentration level associated coefficient of variations...
                    // but first, map records to available concentration Levels
                    ExpIIConcLevelToRecordsMapper expIIConcLevelToRecordsMapper = new ExpIIConcLevelToRecordsMapper();
                    HashMap<String, LinkedList<PeptideRecord>> expIIConcLevelToRecordsMap =
                            expIIConcLevelToRecordsMapper.mapExperimentConcLevelToRecords(tMappedPeptideRecords);
                    Set<String> concLevels = expIIConcLevelToRecordsMap.keySet();
                    
                    // map concetrationLevel to associated CV object...
                    HashMap<String, ExpIIConcLevelCoefficientOfVariation> concLevelToCoeffficientsMap = 
                            new HashMap<String, ExpIIConcLevelCoefficientOfVariation>();
                    for(String concLevel : concLevels){
                        // get transition associated concentration level mapped peptide records...
                        LinkedList<PeptideRecord> tConcLevelMappedRecords = expIIConcLevelToRecordsMap.get(concLevel);
                        ExpIIConcLevelCoefficientOfVariation expIIConcLevelCV = 
                                new ExpIIConcLevelCoefficientOfVariation(transitionId, concLevel,
                                                                            tConcLevelMappedRecords, //could be individual transition or summed
                                                                                config,
                                                                                    logger);
                        concLevelToCoeffficientsMap.put(concLevel, expIIConcLevelCV);                       
                    }
                    // create miniValidationOfRepeatability object
                    ExpIIValidationIdAndConcLevelsCoefs miniValidation = 
                            new ExpIIValidationIdAndConcLevelsCoefs(transitionId, concLevelToCoeffficientsMap);
                    // map derived concentration level associated cvs to id (in this case transition...
                    idToConcLevelsCoefsMap.put(transitionId, miniValidation);                                 
                }                
                break;  
                
            case SUMMED:
                
                break;
                
            default: //BOTH
                
                
                
        }
        
        return idToConcLevelsCoefsMap;
    }
}


