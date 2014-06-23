/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus;

import ios.QCEstimatesPrinter;
import ios.ConfigurationFileReader;
import ios.*;
import java.io.File;
import mrmplus.statistics.PeptideQCEstimator;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author paiyeta1
 */
public class MRMPlus {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        
        System.out.println("Starting...");
        // TODO code application logic here
        /*
         *  
         *  default config...
            header=TRUE
            inputFile=./etc/data/inputFile.txt
            peptidesMonitored=43
            noOftransitions=3
            totalBlanks=9
            replicates=3
            serialDilutions=7
            computeLOD=TRUE
            computeLLOQ=TRUE
            fitCurve=TRUE
            computeLinearity=FALSE
            computeCarryOver=FALSE
            computePartialValidationOfSpecificity=FALSE
            computeULOQ=FALSE
            outputDirectory=./etc/text
            peptidesResultsOutputted=BOTH
            log=TRUE
         * 
         */
        
        LinkedList<PeptideRecord> peptideRecords;
        HashMap<String, LinkedList<PeptideRecord>> pepToRecordsMap;
        
        //read configuration(s)...
        System.out.println("Reading configuration file...");
        ConfigurationFileReader configReader = new ConfigurationFileReader();
        HashMap<String, String> config = configReader.readConfig("./MRMPlus.config");
        
        long start_time = new Date().getTime();
        //instantiate a loggerFile
        String logFile = "./logs" + File.separator + start_time + ".log"; 
        //File loggerFile = new File(logFile);
        PrintWriter logWriter = new PrintWriter(logFile);
        
        //read inputFile...
        System.out.println("Reading 'skyline' preprocessed data input file...");
        logWriter.println("Reading 'skyline' preprocessed data input file...");        
        InputFileReader inFileReader = new InputFileReader();
        String inputFile = config.get("inputFile"); //can be alternative derived 
        peptideRecords = inFileReader.readInputFile(inputFile, config, logWriter);
        
        //associate metadata-info with peptide records
        // Get associated information - metadata...
        System.out.println("Reading metadata file...");
        logWriter.println("Reading metadata file...");
        String metadataFile = config.get("metadataFile");
        MetadataFileReader metadataFileReader = new MetadataFileReader();
        LinkedList<MRMRunMeta> metadata = metadataFileReader.readFile(metadataFile, logWriter);
        
        // map replicate name to meta-info; the replicate name is used in this case because it is unique and could
        // be used to associate metadata to peptide record as we'll use subsequently...
        System.out.println("Mapping 'replicateName'attribute to 'MRMRunMeta' object...");
        logWriter.println("Mapping 'replicateName'attribute to 'MRMRunMeta' object...");
        ExperimentMetadataMapper metadatamapper = new ExperimentMetadataMapper();
        HashMap<String, MRMRunMeta> replicateNameToMetadataMap = 
                metadatamapper.mapReplicateNameToMetadata(metadata);
        logWriter.println("  " + replicateNameToMetadataMap.keySet().size() + " MRMRunMeta objects found in metadata file...");
        
        // update peptideRecords with metadata info...
        System.out.println("Updating peptide records...");
        logWriter.println("Updating peptide records...");
        PeptideRecordsUpdater updater = new PeptideRecordsUpdater();
        updater.updatePeptideRecords(peptideRecords, replicateNameToMetadataMap, logWriter);
        
        // get associated [spikedIn] serial dilution concentration...
        System.out.println("Reading dilution file...");
        logWriter.println("Reading dilution file...");
        String dilutionFile = config.get("dilutionFile");
        DilutionFileReader dilFileReader = new DilutionFileReader();
        HashMap<String, Double> pointToDilutionMap = dilFileReader.readFile(dilutionFile, logWriter);
        
        // update respective peptideRecord with associated point dilution
        System.out.println("Updating peptide records...");
        logWriter.println("Updating peptide records...");
        updater.updatePeptideRecordsDilutions(peptideRecords, pointToDilutionMap, logWriter);
        
        
        //map peptides to records...
        System.out.println("Mapping peptide sequence to peptide record...");
        logWriter.println("Mapping peptide sequence to peptide record...");
        PeptideToRecordsMapper mapper = new PeptideToRecordsMapper();
        pepToRecordsMap = mapper.mapPeptideToRecord(peptideRecords);
        
        //compute statistics results...
        System.out.println("Estimating MRMPlus QCs...");
        logWriter.println("Estimating MRMPlus QCs...");
        PeptideQCEstimator qcEstimator = new PeptideQCEstimator();
        HashMap<String, LinkedList<PeptideResult>> peptideQCEstimates = 
                qcEstimator.estimatePeptidesQCs(pepToRecordsMap, pointToDilutionMap, config, logWriter);
        
        //Print results
        System.out.println("Printing MRMPlus QC estimates...");
        logWriter.println("Printing MRMPlus QC estimates...");
        QCEstimatesPrinter printer = new QCEstimatesPrinter();
        printer.printMRMPlusEstimates(peptideQCEstimates, config);
        
        
        System.out.println("...Done!!!");
        logWriter.println("...Done!!!");
        
        Date end_time = new Date();
        long end = end_time.getTime();
        logWriter.println("End: " + end + ": " + end_time.toString());
        logWriter.println("Total time: " + (end - start_time) + " milliseconds; " + 
                        TimeUnit.MILLISECONDS.toMinutes(end - start_time) + " min(s), "
                        + (TimeUnit.MILLISECONDS.toSeconds(end - start_time) - 
                           TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(end - start_time))) + " seconds.");
        
        logWriter.close();
        
    }
    
    
    
}
