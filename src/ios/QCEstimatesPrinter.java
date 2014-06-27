/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ios;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import mrmplus.PeptideResult;
import mrmplus.statistics.resultobjects.CoefficientOfVariation;
import mrmplus.statistics.resultobjects.LimitOfDetection;
import mrmplus.statistics.resultobjects.LowerLimitOfQuantification;

/**
 *
 * @author paiyeta1
 */
public class QCEstimatesPrinter {

    public void printMRMPlusEstimates(HashMap<String, LinkedList<PeptideResult>> peptideQCEstimates, 
            HashMap<String, String> config) throws FileNotFoundException {
        //throw new UnsupportedOperationException("Not yet implemented");
        String inputFile = config.get("preprocessedFile");
        String outputDir = config.get("outputDirectory");
        String outputFileName = new File(inputFile).getName().replace(".txt", "") + ".mrmplus";
        String outputFile = outputDir + File.separator + outputFileName;
        
        PrintWriter printer = new PrintWriter(outputFile);
        printHeader(printer, config); //printHeader
        //print body
        Set<String> peptideSequences = peptideQCEstimates.keySet();
        for(String peptideSequence : peptideSequences){
            LinkedList<PeptideResult> peptideResults = peptideQCEstimates.get(peptideSequence);
            for(PeptideResult peptideResult : peptideResults){
                printPeptideRowResult(peptideResult, printer, config);
            }
        }      
        
        printer.close();
    }

    private void printHeader(PrintWriter printer, HashMap<String, String> config) {
        // based off configuration file options...
        printer.print("Peptide" + "\t" + "Transition");
        // ************************ //
        // *** Defaults to TRUE ***
        // ************************ //
        //Limit of Detection.
        if(config.get("computeLOD").equalsIgnoreCase("TRUE")){
            // locally defined values...
            //printer.print("\t" + "LOD.transitionID");
            printer.print("\t" + "LOD.average");
            printer.print("\t" + "LOD.stDeviation");
            printer.print("\t" + "LOD.value");
            printer.print("\t" + "LOD.usedMinSpiked");
            printer.print("\t" + "LOD.zeroFlagged");
        }
        //Lower Limit of Quantitation.
        if(config.get("computeLLOQ").equalsIgnoreCase("TRUE")){
            //printer.print("\t" + "LLOQ.transitionID");
            printer.print("\t" + "LLOQ.CaliPoint");
            printer.print("\t" + "LLOQ.Coefficient");
            printer.print("\t" + "LLOQ.MeanValue");
            printer.print("\t" + "LLOQ.StDeviation");
        }
        
        //Fit Curve...
        if(config.get("fitCurve").equalsIgnoreCase("TRUE")){
            throw new UnsupportedOperationException("Not yet implemented");
        }
        
        // ************************* //
        // *** Defaults to FALSE ***
        // ************************* //
        //Estimate Upper Limit of Quantitation.
        if(config.get("computeULOQ").equalsIgnoreCase("TRUE")){
             throw new UnsupportedOperationException("Not yet implemented");           
        }
        
        //Estimate Linearity.
        if(config.get("computeLinearity").equalsIgnoreCase("TRUE")){
            throw new UnsupportedOperationException("Not yet implemented");
        }
        //Estimate Carry-Over.
        if(config.get("computeCarryOver").equalsIgnoreCase("TRUE")){
            throw new UnsupportedOperationException("Not yet implemented");
        }
        //Partially validate Specificity.
        if(config.get("computePartialValidationOfSpecificity").equalsIgnoreCase("TRUE")){
            throw new UnsupportedOperationException("Not yet implemented");
        }
        
        printer.print("\n");
    }

    private void printPeptideRowResult(PeptideResult peptideResult, PrintWriter printer, HashMap<String, String> config) {
        String peptideSequence = peptideResult.getPeptideSequence();
        try{ 
            printer.print(peptideSequence + "\t" + 
                        peptideResult.getTransitionID());
            // ************************ //
            // *** Defaults to TRUE ***
            // ************************ //
            //Limit of Detection.
            if(config.get("computeLOD").equalsIgnoreCase("TRUE")){

                LimitOfDetection lod = peptideResult.getLimitOfDetection();

                printer.print("\t" + lod.getAverage()); // "average");
                printer.print("\t" + lod.getStandardDeviation()); //"standardDeviation");
                printer.print("\t" + lod.getLimitOfDetection()); //"limitOfDetectionValue");
                printer.print("\t" + lod.usedMinSpikedInConcentration()); //"usedMinSpikedInConcentration");
                printer.print("\t" + lod.isZeroValueFlagged()); //"zeroFlagged");

            }
            //Lower Limit of Quantitation.
            if(config.get("computeLLOQ").equalsIgnoreCase("TRUE")){
                LowerLimitOfQuantification lloq = peptideResult.getLowerLimitOfQuantification();
                CoefficientOfVariation coef = lloq.getCoefficientOfVariation();
                if(coef != null){
                    printer.print("\t" + coef.getCalibrationPoint()); // "LLOQ.CaliPoint");
                    printer.print("\t" + coef.getCoef()); //"LLOQ.Coefficient");
                    printer.print("\t" + coef.getMean()); //"LLOQ.MeanValue");
                    printer.print("\t" + coef.getSd()); //"LLOQ.StDeviation");
                } else {
                    printer.print("\tNA"); // "LLOQ.CaliPoint");
                    printer.print("\tNA"); //"LLOQ.Coefficient");
                    printer.print("\tNA"); //"LLOQ.MeanValue");
                    printer.print("\tNA"); //"LLOQ.StDeviation");
                }

            }

            //Fit Curve...
            if(config.get("fitCurve").equalsIgnoreCase("TRUE")){
                throw new UnsupportedOperationException("Not yet implemented");
            }

            // ************************* //
            // *** Defaults to FALSE ***
            // ************************* //
            //Estimate Upper Limit of Quantitation.
            if(config.get("computeULOQ").equalsIgnoreCase("TRUE")){
                throw new UnsupportedOperationException("Not yet implemented");           
            }

            //Estimate Linearity.
            if(config.get("computeLinearity").equalsIgnoreCase("TRUE")){
                throw new UnsupportedOperationException("Not yet implemented");
            }
            //Estimate Carry-Over.
            if(config.get("computeCarryOver").equalsIgnoreCase("TRUE")){
                throw new UnsupportedOperationException("Not yet implemented");
            }
            //Partially validate Specificity.
            if(config.get("computePartialValidationOfSpecificity").equalsIgnoreCase("TRUE")){
                throw new UnsupportedOperationException("Not yet implemented");
            }

            printer.print("\n");
        
        }catch(Exception ex){
            System.out.println(peptideSequence);
            ex.printStackTrace();
        }
        
    }
        
    
}
