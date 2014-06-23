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
public class ExperimentMetadataMapper {
    
    public HashMap<String, MRMRunMeta> mapReplicateNameToMetadata(LinkedList<MRMRunMeta> metadata) {
        HashMap<String, MRMRunMeta> metaMap = new HashMap<String, MRMRunMeta>();
        for(MRMRunMeta runMeta : metadata){
            String replicateName = runMeta.getReplicateName();
            metaMap.put(replicateName, runMeta);
        }       
        return metaMap;
    }
    
}
