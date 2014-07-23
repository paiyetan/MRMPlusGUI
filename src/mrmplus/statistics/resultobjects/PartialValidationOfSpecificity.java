/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.resultobjects;

/**
 *
 * @author paiyeta1
 */
public class PartialValidationOfSpecificity {
    
    private String transitionID;
    private boolean hasValueOverLLOQ; //has value(s) over LLOQ
    private double maxPeakRatioDevFromMean; // maximum peak ratio deviation from the mean
    private double meanAtMaxPeakRatioDeviation; // mean peak ratio at maximum peak ratio deviation from the mean
    private String cali_Point; // calibration point at maximum peak deviation fromo the mean

    public PartialValidationOfSpecificity(String transitionID, boolean hasValueOverLLOQ, 
                double maxPeakRatioDevFromMean, double meanAtMaxPeakRatioDeviation, String cali_Point) {
        this.transitionID = transitionID;
        this.hasValueOverLLOQ = hasValueOverLLOQ;
        this.maxPeakRatioDevFromMean = maxPeakRatioDevFromMean;
        this.meanAtMaxPeakRatioDeviation = meanAtMaxPeakRatioDeviation;
        this.cali_Point = cali_Point;
    }
    
    public String getTransitionID(){
        return this.transitionID;
    }

    public String getCali_Point() {
        return cali_Point;
    }

    public boolean hasValueOverLLOQ() {
        return hasValueOverLLOQ;
    }

    public double getMaxPeakRatioDevFromMean() {
        return maxPeakRatioDevFromMean;
    }

    public double getMeanAtMaxPeakRatioDeviation() {
        return meanAtMaxPeakRatioDeviation;
    }
    
    
    
    
    
}
