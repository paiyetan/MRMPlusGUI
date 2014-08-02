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
public class PeptideSeqToTransitionsMapper {
    
    public HashMap<String, LinkedList<String>> mapPeptideSequenceToTransitions(LinkedList<PeptideRecord> peptideRecords){
        HashMap<String, LinkedList<String>> peptideSequenceToTransitionsMap = 
                new HashMap<String, LinkedList<String>> ();
        for(PeptideRecord peptideRecord : peptideRecords){
            String peptideSequence = peptideRecord.getPeptideSequence(); // peptide sequence
            String peptideTransition = peptideRecord.getTransition(); // found transition
            if(peptideSequenceToTransitionsMap.containsKey(peptideSequence)){
                LinkedList<String> mappedTransitions = peptideSequenceToTransitionsMap.remove(peptideSequence);
                if(mappedTransitions.contains(peptideTransition)){
                    peptideSequenceToTransitionsMap.put(peptideSequence, mappedTransitions); // simple insert back into map                   
                }else{ // mappedTransition(s) doesn't contain found transition
                    mappedTransitions.add(peptideTransition); // add peptideTransition to LinkedList of mapped elements
                    peptideSequenceToTransitionsMap.put(peptideSequence, mappedTransitions); // and, insert back into map                   
                }
            } else {
                LinkedList<String> mappedTransitions = new LinkedList<String>();
                mappedTransitions.add(peptideTransition);
                peptideSequenceToTransitionsMap.put(peptideSequence, mappedTransitions);
            }           
        }
        return peptideSequenceToTransitionsMap;
    }
}
