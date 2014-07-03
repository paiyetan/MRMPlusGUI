/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.resultobjects;

/**
 *
 * @author paiyeta1
 */
public class Linearity {
    //same as linearity object...
    
    private String transitionID; // could be summed (summation of all transitions associated with peptide sequence) or 
                                 // respectively one of the many number of transitions monitored per peptide.
    private double slope;
    private double intercept;
    private double rsquared;
    
    private double slopeStandardError;
    //private double slopeStErrPercentOfSlope;
    private double midResponseValue;
    private double predictedMidResponseValue;
    

    public Linearity(String transitionID, 
                        double slope, 
                            double intercept, 
                                double rsquared) {
        this.transitionID = transitionID;
        this.slope = slope;
        this.intercept = intercept;
        this.rsquared = rsquared;
    }

    public Linearity(String transitionID, 
                         double slope, 
                             double intercept, 
                                double rsquared, 
                                    double slopeStandardError, 
                                        double midResponseValue, 
                                            double predictedMidResponseValue) {
        this.transitionID = transitionID;
        this.slope = slope;
        this.intercept = intercept;
        this.rsquared = rsquared;
        this.slopeStandardError = slopeStandardError;
        this.midResponseValue = midResponseValue;
        this.predictedMidResponseValue = predictedMidResponseValue;
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

    public double getMidResponseValue() {
        return midResponseValue;
    }

    public double getPredictedMidResponseValue() {
        return predictedMidResponseValue;
    }

    public double getSlopeStandardError() {
        return slopeStandardError;
    }
    
    public double getSlopeStErrPercentOfSlope(){
        double slopeStErrPercentOfSlope = 0;
        slopeStErrPercentOfSlope = (Math.abs(slopeStandardError)/Math.abs(slope)) * 100;
        return slopeStErrPercentOfSlope;
    }
    
    public double getDiffPercentOfPredicted(){
        double percent = 0;
        percent = (Math.abs(predictedMidResponseValue - midResponseValue)/predictedMidResponseValue) * 100;
        return percent;
    }
    
    
    
    
    
}
