/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.mappers;

import java.util.HashMap;
import java.util.LinkedList;
import mrmplus.MRMRunMeta;

/**
 *
 * @author paiyeta1
 */
public class ExpIMetadataMapper {
    
    public HashMap<String, MRMRunMeta> mapReplicateNameToMetadata(LinkedList<MRMRunMeta> metadata) {
        HashMap<String, MRMRunMeta> metaMap = new HashMap<String, MRMRunMeta>();
        for(MRMRunMeta runMeta : metadata){
            String replicateName = runMeta.getReplicateName();
            metaMap.put(replicateName, runMeta);
        }       
        return metaMap;
    }
    
}
