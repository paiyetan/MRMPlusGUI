/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ios;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Set;
import mrmplus.statistics.resultobjects.experimentii.ExpIIMiniValidationOfRepeatabilityResult;
import mrmplus.statistics.resultobjects.experimentii.ExpIIPeptidePartialValidationOfSpecificity;
import mrmplus.statistics.resultobjects.experimentii.ExpIIPeptideValidatedLLOQ;

/**
 *
 * @author paiyeta1
 */
public class RepeatabilityEstimatesPrinter {

    public void printMRMPlusRepeatabilityEstimates(HashMap<String, 
                                ExpIIMiniValidationOfRepeatabilityResult> peptideRepeatabilityValidationResultMap, 
                                        HashMap<String, String> config,
                                            String outputId) throws FileNotFoundException {
        //throw new UnsupportedOperationException("Not yet implemented");
        String outputDir = config.get("outputDirectory");
        String outputFileName = outputId + ".repeatability.mrmplus";
        String outputFile = outputDir + File.separator + outputFileName;
        
        PrintWriter printer = new PrintWriter(outputFile);
        //printHeader
        printHeader(printer);
        
        
        //printEachPeptideAssociatedValues
        Set<String> peptideSequences = peptideRepeatabilityValidationResultMap.keySet();
        for(String peptideSequence : peptideSequences){
            //for each peptide Sequence
            ExpIIMiniValidationOfRepeatabilityResult peptideAssResult = 
                    peptideRepeatabilityValidationResultMap.get(peptideSequence);
            HashMap<String, ExpIIPeptideValidatedLLOQ> subIdsToValidatedLLOQMap = 
                    peptideAssResult.getSubIdsToValidatedLLOQMap();
            HashMap<String, ExpIIPeptidePartialValidationOfSpecificity>  subIdsToPartialValidationOfSpecMap = 
                    peptideAssResult.getSubIdsToPartialValidationOfSpecMap();
            //print summed Associated values..
            //print peptideSequence
            printer.print(peptideSequence + "\t");
            // print summed-transition associated variables
            printer.print("SUMMED" + "\t");
            // CV Values
            ExpIIPeptideValidatedLLOQ summedvLLOQ = subIdsToValidatedLLOQMap.get("SUMMED");
            printer.print(summedvLLOQ.getConcentrationLevel() + "\t");//"ValidatedLLOQ.concLevel\t"
            printer.print(summedvLLOQ.getIntraAssayCV() + "\t");//"ValidatedLLOQ.intraAssayCV\t"
            printer.print(summedvLLOQ.getInterAssayCV() + "\t");//"ValidatedLLOQ.intraAssayCV\t"
            printer.print(summedvLLOQ.getTotalCV() + "\t");//"ValidatedLLOQ.intraAssayCV\t" 
            // PVSpec values
            printer.print("NA" + "\t");
            printer.print("NA" + "\t");
            printer.print("NA" + "\t");
            printer.print("NA" + "\n");
            
            //print other peptide transitions associated result rows
            Set<String> subIds = subIdsToPartialValidationOfSpecMap.keySet();
            for(String subId : subIds){
                if(subId.equalsIgnoreCase("SUMMED")==false){
                    // print other associated values...
                    //print peptideSequence
                    printer.print(peptideSequence + "\t");
                    // print summed-transition associated variables
                    printer.print(subId + "\t");
                    // CV Values
                    ExpIIPeptideValidatedLLOQ tVLLOQ = subIdsToValidatedLLOQMap.get(subId);
                    printer.print(tVLLOQ.getConcentrationLevel() + "\t");
                    printer.print(tVLLOQ.getIntraAssayCV() + "\t");
                    printer.print(tVLLOQ.getInterAssayCV() + "\t");
                    printer.print(tVLLOQ.getTotalCV() + "\t");
                    // PVSpec values
                    ExpIIPeptidePartialValidationOfSpecificity pVSpec = 
                            subIdsToPartialValidationOfSpecMap.get(subId);
                    printer.print(pVSpec.getMaxDeviation() + "\t"); //"PVSpec.maxDeviation\t" 
                    printer.print(pVSpec.getMeanAtMaxDeviation() + "\t"); //"PVSpec.meanAtMaxDev\t"
                    printer.print(pVSpec.getDayAtMaxDeviation() + "\t"); //"PVSpec.dayAtMaxDev\t"
                    printer.print(pVSpec.getConcLevelAtMaxDeviation() + "\n"); //"PVSpec.concAtMaxDev"                   
                }
            }
            
        }
        
        
        printer.close();       
    }

    private void printHeader(PrintWriter printer) {
        //throw new UnsupportedOperationException("Not yet implemented");
        printer.println("Peptide\t" +
                        "Transition\t" + 
                        "ValidatedLLOQ.concLevel\t" +
                        "ValidatedLLOQ.intraAssayCV\t" + 
                        "ValidatedLLOQ.interAssayCV\t" + 
                        "ValidatedLLOQ.totalCV\t" + 
                        "PVSpec.maxDeviation\t" +  
                        "PVSpec.meanAtMaxDev\t" + 
                        "PVSpec.dayAtMaxDev\t" + 
                        "PVSpec.concLevelAtMaxDev"
                        );
    }
}
