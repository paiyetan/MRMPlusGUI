/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ios;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import mrmplus.PeptideRecord;


/**
 *
 * @author paiyeta1
 */
public class InputFileReader {
    
    /*
     * 
     *  PeptideSequence	
        ReplicateName	
        PrecursorCharge	
        ProductCharge	
        FragmentIon	
        light PrecursorMz	
        light ProductMz	
        light RetentionTime	
        light Area	
        heavy PrecursorMz	
        heavy ProductMz	
        heavy RetentionTime	
        heavy Area
     * 
     * 
     */
    
    public LinkedList<PeptideRecord> readInputFile(String inputFile, HashMap<String, String> config,
                                                        PrintWriter logWriter) throws FileNotFoundException, IOException{
        LinkedList<PeptideRecord> peptideRecords = new LinkedList<PeptideRecord>();
        BufferedReader input = new BufferedReader(new FileReader(new File(inputFile)));        

        int lines_read = 0;
        String line;
        FileAttributeIndex[] fileAttrIndeces = null;
        int rowIndex = 0;
        int dataRows = computeDataRows(config, logWriter);
        System.out.println(" Reading " + dataRows + " data records of peptides to evaluate..." );
        logWriter.println(" Reading " + dataRows + " data records of peptides to evaluate...");
        while ((line = input.readLine()) != null) {
            lines_read++;
            if (lines_read == 1){
                // get the attributes reported for input file
                String[] lineContent = line.split("\t");
                fileAttrIndeces = new FileAttributeIndex[lineContent.length];
                // get the indeces for the reported attributes
                for (int i = 0; i < lineContent.length; i++){
                    fileAttrIndeces[i] = new FileAttributeIndex((lineContent[i]),i); 
                }               
            }
            if(lines_read != 1){
                rowIndex++;
                if(rowIndex <= dataRows){
                    String[] lineContent = line.split("\\t");
                    String PeptideSequence = lineContent[getAttributeIndex(fileAttrIndeces,"PeptideSequence")];	
                    String ReplicateName = lineContent[getAttributeIndex(fileAttrIndeces,"ReplicateName")];	
                    int PrecursorCharge = Integer.parseInt(lineContent[getAttributeIndex(fileAttrIndeces,"PrecursorCharge")]);	
                    int ProductCharge =  Integer.parseInt(lineContent[getAttributeIndex(fileAttrIndeces,"ProductCharge")]);	
                    String FragmentIon = lineContent[getAttributeIndex(fileAttrIndeces,"FragmentIon")];	
                    double lightPrecursorMz = Double.parseDouble(lineContent[getAttributeIndex(fileAttrIndeces,"light PrecursorMz")]);	
                    double lightProductMz = Double.parseDouble(lineContent[getAttributeIndex(fileAttrIndeces,"light ProductMz")]);	
                    double lightRetentionTime = Double.parseDouble(lineContent[getAttributeIndex(fileAttrIndeces,"light RetentionTime")]);	
                    double lightArea = Double.parseDouble(lineContent[getAttributeIndex(fileAttrIndeces,"light Area")]);	
                    double heavyPrecursorMz = Double.parseDouble(lineContent[getAttributeIndex(fileAttrIndeces,"heavy PrecursorMz")]);	
                    double heavyProductMz = Double.parseDouble(lineContent[getAttributeIndex(fileAttrIndeces,"heavy ProductMz")]);	
                    double heavyRetentionTime = Double.parseDouble(lineContent[getAttributeIndex(fileAttrIndeces,"heavy RetentionTime")]);	
                    double heavyArea = Double.parseDouble(lineContent[getAttributeIndex(fileAttrIndeces,"heavy Area")]);
                
                    peptideRecords.add(new PeptideRecord(PeptideSequence, ReplicateName, PrecursorCharge,
                                                            ProductCharge, FragmentIon, lightPrecursorMz, 
                                                                lightProductMz, lightRetentionTime, lightArea,
                                                                    heavyPrecursorMz, heavyProductMz, heavyRetentionTime,
                                                                        heavyArea));
                    if((rowIndex % 500) == 0){
                        System.out.println("  " + rowIndex + " peptide records read...");
                        logWriter.println("  " + rowIndex + " peptide records read...");
                    }    
                }
            }
        }
        logWriter.println("  " + peptideRecords.size() + " peptide records read...");
        return peptideRecords;
    }  

    private int computeDataRows(HashMap<String, String> config, PrintWriter log) {
        //throw new UnsupportedOperationException("Not yet implemented");
        int dataRows = 0;
        /*
         * Get computation variables from config file...
            peptidesMonitored=43
            noOftransitions=3
            totalBlanks=9
            replicates=3
            serialDilutions=7
         */
        int peptidesMonitored = Integer.parseInt(config.get("peptidesMonitored"));
        int transitions = Integer.parseInt(config.get("noOftransitions"));
        int totalBlanks = Integer.parseInt(config.get("totalBlanks"));
        int replicates = Integer.parseInt(config.get("replicates"));
        int serialDilutions = Integer.parseInt(config.get("serialDilutions"));
        // output values
        System.out.println(" Peptides Monitored: " + peptidesMonitored);
        System.out.println(" Number of Transtions/peptide: " + transitions);
        System.out.println(" Number of Blanks: " + totalBlanks);
        System.out.println(" Number of Replicates performed: " + replicates);
        System.out.println(" Number of Serial Dilutions: " + serialDilutions);
        
        //log info...
        log.println(" Peptides Monitored: " + peptidesMonitored);
        log.println(" Number of Transtions/peptide: " + transitions);
        log.println(" Number of Blanks: " + totalBlanks);
        log.println(" Number of Replicates performed: " + replicates);
        log.println(" Number of Serial Dilutions: " + serialDilutions);
        // compute data rows
        dataRows = (((serialDilutions * replicates) + totalBlanks) * transitions * peptidesMonitored);
        
        return dataRows;
    }
     
    
    
    private int getAttributeIndex(FileAttributeIndex[] fileAttrIndeces, String attr) {
        //throw new UnsupportedOperationException("Not yet implemented");
        int attrIndex = 0;
        for(int i = 0; i < fileAttrIndeces.length; i++){
            if(attr.equalsIgnoreCase(fileAttrIndeces[i].getAttribute())){
                attrIndex = i;
            }
        }       
        return attrIndex;
    }
    
    class FileAttributeIndex {
    
        private String attribute;
        private int index;

        public FileAttributeIndex(String attr, int ind){
            attribute = attr;
            index = ind;
        }

        public String getAttribute() {
            return attribute;
        }

        public int getIndex() {
            return index;
        }
    
    }
    
    
}
