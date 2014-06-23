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
public class ExperimentCalibrationPointToRecordsMapper {

    public HashMap<String, LinkedList<PeptideRecord>> mapCalibrationPointsToRecords(LinkedList<PeptideRecord> transitionRecords) {
        HashMap<String, LinkedList<PeptideRecord>> points2Recs = new HashMap<String, LinkedList<PeptideRecord>>();
        for(PeptideRecord peptideRecord : transitionRecords){
            String caliPoint = peptideRecord.getCalibrationPoint();
            if(caliPoint.equalsIgnoreCase("NA")==false){
                if(points2Recs.containsKey(caliPoint)){
                    LinkedList<PeptideRecord> mappedRecords = points2Recs.remove(caliPoint);
                    mappedRecords.add(peptideRecord);
                    points2Recs.put(caliPoint, mappedRecords);
                } else {
                    LinkedList<PeptideRecord> mappedRecords = new LinkedList<PeptideRecord>();
                    mappedRecords.add(peptideRecord);
                    points2Recs.put(caliPoint, mappedRecords);
                }
                
            }
        }        
        return points2Recs;
    }
    
}
