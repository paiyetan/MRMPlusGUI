/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.mappers;

import java.util.HashMap;
import java.util.LinkedList;
import mrmplus.PeptideRecord;

/**
 *
 * @author paiyeta1
 * 
 * this is different from CalibrationPointToRecordsMapper
 */
public class ExpIIConcLevelToRecordsMapper {
    
    
    public HashMap<String, LinkedList<PeptideRecord>> mapExperimentConcLevelToRecords(LinkedList<PeptideRecord> peptideRecords){
        
        HashMap<String, LinkedList<PeptideRecord>> expConcLevelToRecords = new HashMap<String, LinkedList<PeptideRecord>>();
        for(PeptideRecord peptideRecord : peptideRecords){
            String expConcLevel = peptideRecord.getConcentrationLevel();
            if(expConcLevelToRecords.containsKey(expConcLevel)){
                LinkedList<PeptideRecord> mappedRecords = expConcLevelToRecords.remove(expConcLevel);
                mappedRecords.add(peptideRecord);
                expConcLevelToRecords.put(expConcLevel, mappedRecords);
            }else{
                LinkedList<PeptideRecord> mappedRecords = new LinkedList<PeptideRecord>();
                mappedRecords.add(peptideRecord);
                expConcLevelToRecords.put(expConcLevel, mappedRecords);
            }           
        }               
        return expConcLevelToRecords;    
             
    }
    
}
