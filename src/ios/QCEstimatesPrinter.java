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
import mrmplus.statistics.resultobjects.*;

/**
 *
 * @author paiyeta1
 */
public class QCEstimatesPrinter {

    public void printMRMPlusEstimates(HashMap<String, LinkedList<PeptideResult>> peptideQCEstimates, 
                                            HashMap<String, String> config,
                                                String outputId) 
                throws FileNotFoundException {
        //throw new UnsupportedOperationException("Not yet implemented");
        //String inputFile = config.get("preprocessedFile");
        String outputDir = config.get("outputDirectory");
        //String outputFileName = new File(inputFile).getName().replace(".txt", "") + ".mrmplus";
        String outputFileName = outputId + ".response.mrmplus";
        String outputFile = outputDir + File.separator + outputFileName;
        
        PrintWriter printer = new PrintWriter(outputFile);
        //printHeader
        printHeader(printer, config); 
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
        // Limit of Detection.
        if(config.get("computeLOD").equalsIgnoreCase("TRUE")){
            // locally defined values...
            //printer.print("\t" + "LOD.transitionID");
            printer.print("\t" + "LOD.average");
            printer.print("\t" + "LOD.stDeviation");
            printer.print("\t" + "LOD.value");
            printer.print("\t" + "LOD.usedSpiked");
            printer.print("\t" + "LOD.zeroFlagged");
            printer.print("\t" + "LOD.concPointUsed");
            printer.print("\t" + "LOD.undetectable");
        }
        // Lower Limit of Quantitation.
        if(config.get("computeLLOQ").equalsIgnoreCase("TRUE")){
            //printer.print("\t" + "LLOQ.transitionID");
            printer.print("\t" + "LLOQ.CaliPoint");
            printer.print("\t" + "LLOQ.Coefficient");
            printer.print("\t" + "LLOQ.MeanValue");
            printer.print("\t" + "LLOQ.StDeviation");
        }
        // Linearity.
        if(config.get("computeLinearity").equalsIgnoreCase("TRUE")){
            //throw new UnsupportedOperationException("Not yet implemented");
            printer.print("\t" + "Curve.Slope");
            printer.print("\t" + "Curve.Intercept");
            printer.print("\t" + "Curve.Rsquared");
            printer.print("\t" + "Curve.Predicted");
            printer.print("\t" + "Curve.Observed");
            printer.print("\t" + "Curve.Diff(%)");
            printer.print("\t" + "Curve.SlopeStdErr");
            printer.print("\t" + "Curve.SlopeStdErr(%)");
        }
        
        
        // ************************* //
        // *** Defaults to FALSE ***
        // ************************* //
        
        // Carry-Over.
        if(config.get("computeCarryOver").equalsIgnoreCase("TRUE")){
            //throw new UnsupportedOperationException("Not yet implemented");
            printer.print("\t" + "CarryOver.maxPeakArea");
            printer.print("\t" + "CarryOver.postMaxPeakArea");
            printer.print("\t" + "CarryOver.carryOver(%)");
            
        }
        // Partially validate Specificity.
        if(config.get("computePartialValidationOfSpecificity").equalsIgnoreCase("TRUE")){
            //throw new UnsupportedOperationException("Not yet implemented");
            printer.print("\t" + "PVSpec.hasValuesOverLLOQ");
            printer.print("\t" + "PVSpec.pointOfMaxDev (Cali_Point)");
            printer.print("\t" + "PVSpec.pointOfMaxDev (Mean)");
            printer.print("\t" + "PVSpec.maxDeviation (%)");
        }
        
        // Upper Limit of Quantitation.
        if(config.get("computeULOQ").equalsIgnoreCase("TRUE")){
            //throw new UnsupportedOperationException("Not yet implemented");
            printer.print("\t" + "ULOQ.CaliPoint");
            printer.print("\t" + "ULOQ.Coefficient");
            printer.print("\t" + "ULOQ.MeanValue");
            printer.print("\t" + "ULOQ.StDeviation");
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
            // Limit of Detection.
            if(config.get("computeLOD").equalsIgnoreCase("TRUE")){

                LimitOfDetection lod = peptideResult.getLimitOfDetection();

                printer.print("\t" + lod.getAverage()); // "average");
                printer.print("\t" + lod.getStandardDeviation()); //"standardDeviation");
                printer.print("\t" + lod.getLimitOfDetection()); //"limitOfDetectionValue");
                printer.print("\t" + lod.usedSpikedInConcentration()); //"usedSpikedInConcentration");
                printer.print("\t" + lod.hasZeroValueFlagged()); //"zeroFlagged");
                printer.print("\t" + lod.getConcentrationPointUsed());//"LOD.concPointUsed");
                printer.print("\t" + lod.isUndetectable());//"LOD.undetectable");

            }
            // Lower Limit of Quantitation.
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
                    printer.print("\tNA"); // "LLOQ.Coefficient");
                    printer.print("\tNA"); // "LLOQ.MeanValue");
                    printer.print("\tNA"); // "LLOQ.StDeviation");
                }

            }

            // Linearity.
            if(config.get("computeLinearity").equalsIgnoreCase("TRUE")){
                //throw new UnsupportedOperationException("Not yet implemented");
                Linearity linearity = peptideResult.getLinearity();
                printer.print("\t" + linearity.getSlope()); // "Curve.Slope"
                printer.print("\t" + linearity.getIntercept()); // "Curve.Intercept"
                printer.print("\t" + linearity.getRsquared()); // "Curve.Rsquared"
                printer.print("\t" + linearity.getPredictedMidResponseValue()); // "Curve.Predicted"
                printer.print("\t" + linearity.getMidResponseValue()); // "Curve.Observed"
                printer.print("\t" + linearity.getDiffPercentOfPredicted()); // "Curve.Diff(%)"
                printer.print("\t" + linearity.getSlopeStandardError()); // "Curve.SlopeStdErr"
                printer.print("\t" + linearity.getSlopeStErrPercentOfSlope()); // "Curve.SlopeStdErr(%)"
            }
            
            // ************************* //
            // *** Defaults to FALSE ***
            // ************************* //
            // Upper Limit of Quantitation.
            
            // Carry-Over.
            if(config.get("computeCarryOver").equalsIgnoreCase("TRUE")){
                //throw new UnsupportedOperationException("Not yet implemented");
                CarryOver carryOver = peptideResult.getCarryOver();
                printer.print("\t" + carryOver.getMaxConcPointPeakArea()); // "Curve.Diff(%)"
                printer.print("\t" + carryOver.getPostMaxBlankPeakArea()); // "Curve.SlopeStdErr"
                printer.print("\t" + carryOver.getCarryOverValue()); // "Curve.SlopeStdErr(%)"
            }
            // Partially validate Specificity.
            if(config.get("computePartialValidationOfSpecificity").equalsIgnoreCase("TRUE")){
                //throw new UnsupportedOperationException("Not yet implemented");
                /*
                 *  "PVSpec.hasValuesOverLLOQ"
                    "PVSpec.pointOfMaxDev (Cali_Point)"
                    "PVSpec.pointOfMaxDev (Mean)"
                    "PVSpec.maxDeviation (%)"
                 */
                PartialValidationOfSpecificity pvs = peptideResult.getPartialValidationOfSpecificity();
                printer.print("\t" + pvs.hasValueOverLLOQ());
                printer.print("\t" + pvs.getCali_Point());
                printer.print("\t" + pvs.getMeanAtMaxPeakRatioDeviation());
                printer.print("\t" + pvs.getMaxPeakRatioDevFromMean());
            }
            
            if(config.get("computeULOQ").equalsIgnoreCase("TRUE")){
                //throw new UnsupportedOperationException("Not yet implemented");   
                /*
                 * 
                 */
                UpperLimitOfQuantification uloq = peptideResult.getUpperLimitOfQuantification();
                CoefficientOfVariation coef = uloq.getCoefficientOfVariation();
                if(coef != null){
                    printer.print("\t" + coef.getCalibrationPoint()); // "LLOQ.CaliPoint");
                    printer.print("\t" + coef.getCoef()); //"LLOQ.Coefficient");
                    printer.print("\t" + coef.getMean()); //"LLOQ.MeanValue");
                    printer.print("\t" + coef.getSd()); //"LLOQ.StDeviation");
                } else {
                    printer.print("\tNA"); // "LLOQ.CaliPoint");
                    printer.print("\tNA"); // "LLOQ.Coefficient");
                    printer.print("\tNA"); // "LLOQ.MeanValue");
                    printer.print("\tNA"); // "LLOQ.StDeviation");
                }
            }


            printer.print("\n");
        
        }catch(Exception ex){
            System.out.println(peptideSequence);
            ex.printStackTrace();
        }
        
    }
        
    
}
