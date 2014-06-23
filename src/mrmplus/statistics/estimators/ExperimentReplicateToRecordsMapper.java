/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.estimators;

import java.util.HashMap;
import java.util.LinkedList;
import mrmplus.PeptideRecord;

/**
 *
 * @author paiyeta1
 */
public class ExperimentReplicateToRecordsMapper {

    public HashMap<String, LinkedList<PeptideRecord>> mapReplicatesToRecords(LinkedList<PeptideRecord> transitionRecords) {
        
        HashMap<String, LinkedList<PeptideRecord>> replicate2Records = new HashMap<String, LinkedList<PeptideRecord>>();
        
        for(PeptideRecord peptideRecord : transitionRecords){
            String replicate = peptideRecord.getReplicate();
            if(replicate.equalsIgnoreCase("NA")==false){
                if(replicate2Records.containsKey(replicate)){
                    LinkedList<PeptideRecord> mappedRecords = replicate2Records.remove(replicate);
                    mappedRecords.add(peptideRecord);
                    replicate2Records.put(replicate, mappedRecords);
                } else {
                    LinkedList<PeptideRecord> mappedRecords = new LinkedList<PeptideRecord>();
                    mappedRecords.add(peptideRecord);
                    replicate2Records.put(replicate, mappedRecords);
                }
                
            }
        }
        
        return replicate2Records;
    }
    
}
