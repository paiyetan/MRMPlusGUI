/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.resultobjects;

/**
 *
 * @author paiyeta1
 */
public class LowerLimitOfQuantification {
    
    private String transitionID; // could be summed (summation of all transitions associated with peptide sequence) or 
                                 // respectively one of the many number of transitions monitored per peptide.
    private double blankOnly;
    private double blankPlusLowConc;
    private double rsdLimit;

    public LowerLimitOfQuantification(String transitionID, double blankOnly, double blankPlusLowConc, double rsdLimit) {
        this.transitionID = transitionID;
        this.blankOnly = blankOnly;
        this.blankPlusLowConc = blankPlusLowConc;
        this.rsdLimit = rsdLimit;
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
    
    
    
}
