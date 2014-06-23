/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus;

import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author paiyeta1
 */
public class PeptideToRecordsMapper {
    
    public HashMap<String, LinkedList<PeptideRecord>> mapPeptideToRecord(LinkedList<PeptideRecord> filePeptideRecords){
        HashMap<String, LinkedList<PeptideRecord>> peptideToRecordsMap = new HashMap<String, LinkedList<PeptideRecord>>();
        for(PeptideRecord filePeptideRecord : filePeptideRecords){
            String peptideSequence = filePeptideRecord.getPeptideSequence();
            if(peptideToRecordsMap.containsKey(peptideSequence)){
                LinkedList<PeptideRecord> mappedRecords = peptideToRecordsMap.remove(peptideSequence);
                mappedRecords.add(filePeptideRecord);
                peptideToRecordsMap.put(peptideSequence, mappedRecords);
            } else {
                LinkedList<PeptideRecord> mappedRecords = new LinkedList<PeptideRecord>();
                mappedRecords.add(filePeptideRecord);
                peptideToRecordsMap.put(peptideSequence, mappedRecords);
            }
        }      
        return peptideToRecordsMap;
    }
}
