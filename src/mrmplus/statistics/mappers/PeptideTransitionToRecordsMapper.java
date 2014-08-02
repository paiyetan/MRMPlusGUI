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
public class PeptideTransitionToRecordsMapper {
    
    public  HashMap<String, LinkedList<PeptideRecord>> mapTransitionsToRecords(LinkedList<PeptideRecord> peptideRecords) {
        //throw new UnsupportedOperationException("Not yet implemented");
        HashMap<String, LinkedList<PeptideRecord>> transitionToRecords = new HashMap<String, LinkedList<PeptideRecord>>();
        
        for(PeptideRecord peptideRecord : peptideRecords){
            String transition = peptideRecord.getTransition();
            if(transitionToRecords.containsKey(transition)){
                LinkedList<PeptideRecord> mappedRecords = transitionToRecords.remove(transition);
                mappedRecords.add(peptideRecord);
                transitionToRecords.put(transition, mappedRecords);
            }else{
                LinkedList<PeptideRecord> mappedRecords = new LinkedList<PeptideRecord>();
                mappedRecords.add(peptideRecord);
                transitionToRecords.put(transition, mappedRecords);
            }
        }       
        return transitionToRecords;        
    }
    
}
