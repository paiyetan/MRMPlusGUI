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
public class RepeatabilityInputFileReader {
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
                                                        Logger logger) throws FileNotFoundException, IOException{
        LinkedList<PeptideRecord> peptideRecords = new LinkedList<PeptideRecord>();
        BufferedReader reader = new BufferedReader(new FileReader(new File(inputFile)));        

        int lines_read = 0;
        String line;
        FileAttributeIndex[] fileAttrIndeces = null;
        int rowIndex = 0;
        int dataRows = computeDataRows(config, logger);
        //System.out.println(" Reading " + dataRows + " data records of peptides to evaluate..." );
        logger.println(" Reading " + dataRows + " data records of peptides to evaluate...");
        while ((line = reader.readLine()) != null) {
            lines_read++;
            if (lines_read == 1){
                // get the attributes reported for reader file
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
                        logger.println("  " + rowIndex + " peptide records read...");
                    }    
                }
            }
        }
        reader.close();
        logger.println("  " + peptideRecords.size() + " peptide records read...");
        return peptideRecords;
    }  

    private int computeDataRows(HashMap<String, String> config, Logger logger) {
        //throw new UnsupportedOperationException("Not yet implemented");
        int dataRows = 0;
        /*
         * Get computation variables from config file...
            
            1. repeatabilityNoOfPeptidesMonitored=43
            2. repeatabilityNoOftransitions=3
            3. repeatabiliityNoOfConcentrationLevels=3
            4. repeatabilityOverNoOfDays=5
            5. repeatabilityNoOfReplicatesPerDay=3

         */
        
        
        int peptidesMonitored = Integer.parseInt(config.get("repeatabilityNoOfPeptidesMonitored")); //1
        int transitions = Integer.parseInt(config.get("repeatabilityNoOftransitions")); //2
        int concentrationLevels = Integer.parseInt(config.get("repeatabiliityNoOfConcentrationLevels")); //3.
        int noOfDays = Integer.parseInt(config.get("repeatabilityOverNoOfDays")); //4
        int replicatesPerDay = Integer.parseInt(config.get("repeatabilityNoOfReplicatesPerDay")); //5 
        // output values
        
        //System.out.println(" Peptides Monitored: " + peptidesMonitored);
        //System.out.println(" Number of Transtions/peptide: " + transitions);
        //System.out.println(" Number of Replicates per day performed: " + replicates);
        //System.out.println(" Number of Serial Dilutions: " + serialDilutions);
        
        //log info...
        
        logger.println("\n");
        logger.println("============================");
        logger.println(" EXPERIMENT 2: REPEATABILITY");
        logger.println("============================");
        logger.println(" Peptides Monitored: " + peptidesMonitored);
        logger.println(" Number of Transtions/peptide: " + transitions);
        logger.println(" Number of Concentration levels: " + concentrationLevels);
        logger.println(" Number of Days: " + noOfDays);
        logger.println(" Number of Replicates per day: " + replicatesPerDay);
        logger.println("\n");
        
        // compute data rows
        dataRows = (((replicatesPerDay * noOfDays) * concentrationLevels) * transitions) * peptidesMonitored;       
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
