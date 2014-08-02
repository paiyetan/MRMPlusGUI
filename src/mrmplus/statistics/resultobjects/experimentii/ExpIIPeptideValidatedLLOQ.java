/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.resultobjects.experimentii;

/**
 *
 * @author paiyeta1
 * 
 * Lowest of three concentrations at which total estimated variability is < 20%
 */
public class ExpIIPeptideValidatedLLOQ {
    
    private String concentrationLevel;
    private double totalCV;
    private double intraAssayCV;
    private double interAssayCV;
    // private ExpIIMiniValidationOfRepeatabilityResult minivalidationResult;
    
    public ExpIIPeptideValidatedLLOQ(String concentrationLevel, double totalCV) {
        this.concentrationLevel = concentrationLevel;
        this.totalCV = totalCV;
    }

    public ExpIIPeptideValidatedLLOQ(String concentrationLevel, double totalCV, double intraAssayCV, double interAssayCV) {
        this.concentrationLevel = concentrationLevel;
        this.totalCV = totalCV;
        this.intraAssayCV = intraAssayCV;
        this.interAssayCV = interAssayCV;
    }

    public double getInterAssayCV() {
        return interAssayCV;
    }

    public double getIntraAssayCV() {
        return intraAssayCV;
    }
    
    public double getTotalCV() {
        return totalCV;
    }
    
    public String getConcentrationLevel() {
        return concentrationLevel;
    }   
    
}
