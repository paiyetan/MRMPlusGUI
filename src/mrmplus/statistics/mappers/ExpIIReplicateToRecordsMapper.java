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
public class ExpIIReplicateToRecordsMapper {

    public HashMap<String, LinkedList<PeptideRecord>> mapReplicateToRecords(LinkedList<PeptideRecord> concLevelPeptideRecords) {
        //throw new UnsupportedOperationException("Not yet implemented");
        HashMap<String, LinkedList<PeptideRecord>> replicateToRecords = new HashMap<String, LinkedList<PeptideRecord>>();
        for(PeptideRecord peptideRecord : concLevelPeptideRecords){
            String replicate = peptideRecord.getDayReplicate(); //NB: this a different method call specific to Experiment II 
            if(replicateToRecords.containsKey(replicate)){
                    LinkedList<PeptideRecord> mappedRecords = replicateToRecords.remove(replicate);
                    mappedRecords.add(peptideRecord);
                    replicateToRecords.put(replicate, mappedRecords);
            } else {
                LinkedList<PeptideRecord> mappedRecords = new LinkedList<PeptideRecord>();
                mappedRecords.add(peptideRecord);
                replicateToRecords.put(replicate, mappedRecords);
            }
        }
        return replicateToRecords;
    }
   
}
