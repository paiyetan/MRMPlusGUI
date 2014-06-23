/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.resultobjects;

/**
 *
 * @author paiyeta1
 */
public class CurveFit {
    
    private String transitionID; // could be summed (summation of all transitions associated with peptide sequence) or 
                                 // respectively one of the many number of transitions monitored per peptide.
    private double slope;
    private double intercept;
    private double rsquared;

    public CurveFit(String transitionID, double slope, double intercept, double rsquared) {
        this.transitionID = transitionID;
        this.slope = slope;
        this.intercept = intercept;
        this.rsquared = rsquared;
    }

    public double getIntercept() {
        return intercept;
    }

    public double getRsquared() {
        return rsquared;
    }

    public double getSlope() {
        return slope;
    }

    public String getTransitionID() {
        return transitionID;
    }
    
    
    
}
