/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ios;

import java.io.*;
import java.util.LinkedList;
import mrmplus.MRMRunMeta;
/**
 *
 * @author paiyeta1
 */
public class MetadataFileReader {
    
    /*
     * 
     *  private String RunOrder;	
        private String ReplicateName;	
        private String AnalyteType;	
        private String Replicate;	
        private String CalibrationPoint;

     */
    public LinkedList<MRMRunMeta> readFile(String metadaFile, PrintWriter log) throws FileNotFoundException, IOException{
        LinkedList<MRMRunMeta> metaInfo = new LinkedList<MRMRunMeta>();
        BufferedReader input = new BufferedReader(new FileReader(new File(metadaFile)));        

        int lines_read = 0;
        String line;
        System.out.println(" Reading experiment's metadata information..." );
        log.println(" Reading experiment's metadata information...");
        while ((line = input.readLine()) != null) {
            lines_read++;
            if (lines_read > 1){
                String[] lineArr = line.split("\t");
                String RunOrder = lineArr[0];	
                String ReplicateName = lineArr[1];	
                String AnalyteType = lineArr[2];	
                String Replicate = lineArr[3];	
                String CalibrationPoint = lineArr[4];
                metaInfo.add(new MRMRunMeta(RunOrder, ReplicateName, AnalyteType, Replicate, CalibrationPoint));
            }
        }
        return metaInfo;
    }
}
