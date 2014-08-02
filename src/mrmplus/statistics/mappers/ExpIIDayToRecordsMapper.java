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
 */
public class ExpIIDayToRecordsMapper {
    
    public HashMap<String, LinkedList<PeptideRecord>> mapExperimentDayToRecords(LinkedList<PeptideRecord> peptideRecords){
        
        HashMap<String, LinkedList<PeptideRecord>> expDayToRecords = new HashMap<String, LinkedList<PeptideRecord>>();
        for(PeptideRecord peptideRecord : peptideRecords){
            String expDay = peptideRecord.getAssayDay();
            if(expDayToRecords.containsKey(expDay)){
                LinkedList<PeptideRecord> mappedRecords = expDayToRecords.remove(expDay);
                mappedRecords.add(peptideRecord);
                expDayToRecords.put(expDay, mappedRecords);
            }else{
                LinkedList<PeptideRecord> mappedRecords = new LinkedList<PeptideRecord>();
                mappedRecords.add(peptideRecord);
                expDayToRecords.put(expDay, mappedRecords);
            }           
        }               
        return expDayToRecords;    
             
    }
    
}
