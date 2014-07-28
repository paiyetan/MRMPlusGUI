/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.resultobjects;

/**
 *
 * @author paiyeta1
 */
public class LowerLimitOfQuantification implements Comparable{
    
    private String transitionID; // could be summed (summation of all transitions associated with peptide sequence) or 
                                 // respectively one of the many number of transitions monitored per peptide.
    private double blankOnly;
    private double blankPlusLowConc;
    private double rsdLimit;
    
    private CoefficientOfVariation coefficientOfVariation;

    public LowerLimitOfQuantification(String transitionID, double blankOnly, double blankPlusLowConc, double rsdLimit) {
        this.transitionID = transitionID;
        this.blankOnly = blankOnly;
        this.blankPlusLowConc = blankPlusLowConc;
        this.rsdLimit = rsdLimit;
    }

    public LowerLimitOfQuantification(String transitionID, CoefficientOfVariation coefficientOfVariation) {
        this.transitionID = transitionID;
        this.coefficientOfVariation = coefficientOfVariation;
    }

    public CoefficientOfVariation getCoefficientOfVariation() {
        return coefficientOfVariation;
    }
    
    public double getBlankOnly() {
        return blankOnly;
    }

    public double getBlankPlusLowConc() {
        return blankPlusLowConc;
    }

    public double getRsdLimit() {
        return rsdLimit;
    }

    public String getTransitionID() {
        return transitionID;
    }

    @Override
    public int compareTo(Object o) {
        int comparison = 0;
        LowerLimitOfQuantification obj = (LowerLimitOfQuantification) o;
        int thisCalibrationPointIndex = this.coefficientOfVariation.getCalibrationPointIndex();
        int objCalibrationPointIndex = obj.getCoefficientOfVariation().getCalibrationPointIndex();
        
        if(thisCalibrationPointIndex < objCalibrationPointIndex){
            comparison = -1;
        } else if(thisCalibrationPointIndex > objCalibrationPointIndex){
            comparison = 1;
        }
        
        return comparison;
    }
    
    
    
}
