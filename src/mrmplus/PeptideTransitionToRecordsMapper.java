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
public class PeptideTransitionToRecordsMapper {
    
    public  HashMap<String, LinkedList<PeptideRecord>> mapTransitionsToRecords(LinkedList<PeptideRecord> peptideRecords) {
        //throw new UnsupportedOperationException("Not yet implemented");
        HashMap<String, LinkedList<PeptideRecord>> transitionToRecords = new HashMap<String, LinkedList<PeptideRecord>>();
        
        for(PeptideRecord peptideRecord : peptideRecords){
            String fragmentIon = peptideRecord.getFragmentIon();
            if(transitionToRecords.containsKey(fragmentIon)){
                LinkedList<PeptideRecord> mappedRecords = transitionToRecords.remove(fragmentIon);
                mappedRecords.add(peptideRecord);
                transitionToRecords.put(fragmentIon, mappedRecords);
            }else{
                LinkedList<PeptideRecord> mappedRecords = new LinkedList<PeptideRecord>();
                mappedRecords.add(peptideRecord);
                transitionToRecords.put(fragmentIon, mappedRecords);
            }
        }       
        return transitionToRecords;        
    }
    
}
