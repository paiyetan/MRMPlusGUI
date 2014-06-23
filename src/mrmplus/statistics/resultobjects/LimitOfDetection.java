/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.resultobjects;

/**
 *
 * @author paiyeta1
 */
public class LimitOfDetection {
    
    private String transitionID; // could be summed (summation of all transitions associated with peptide sequence) or 
                                 // respectively one of the many number of transitions monitored per peptide.
    
    // CPTAC MARMAssay development group site defined values...
    private double blankOnly;
    private double blankPlusLowConc;
    private double rsdLimit;
    
    // locally defined values...
    private double average;
    private double standardDeviation;
    private double limitOfDetection;
    private boolean usedMinSpikedInConcentration;
    private boolean zeroValueFlag;

    /**
     * 
     * @param transitionID
     * @param blankOnly
     * @param blankPlusLowConc
     * @param rsdLimit
     */
    
    public LimitOfDetection(String transitionID, double blankOnly, double blankPlusLowConc, double rsdLimit) {
        this.transitionID = transitionID;
        this.blankOnly = blankOnly;
        this.blankPlusLowConc = blankPlusLowConc;
        this.rsdLimit = rsdLimit;
    }
    
    public LimitOfDetection(String transitionID, double average, 
                                    double sd, double lod, boolean usedMinSpikedInConcentration){
        this.transitionID = transitionID;
        this.average = average;
        this.standardDeviation = sd;
        this.limitOfDetection = lod;
        this.usedMinSpikedInConcentration = usedMinSpikedInConcentration;       
    }
    
    public LimitOfDetection(double average, double sd, double lod){
        //this.transitionID = transitionID;
        this.average = average;
        this.standardDeviation = sd;
        this.limitOfDetection = lod;
        //this.usedMinSpikedInConcentration = usedMinSpikedInConcentration;       
    }
    
    public LimitOfDetection(double average, double sd, double lod, boolean zeroFlagged){
        //this.transitionID = transitionID;
        this.average = average;
        this.standardDeviation = sd;
        this.limitOfDetection = lod;
        this.zeroValueFlag = zeroFlagged;       
    }
    
    public void setUsedMinSpikedInConcentration(boolean use){
        usedMinSpikedInConcentration = use;
    }

    public void setTransitionID(String transitionID) {
        this.transitionID = transitionID;
    }

    
    public void setZeroValueFlag(boolean zeroValueFlag) {
        this.zeroValueFlag = zeroValueFlag;
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

    public double getAverage() {
        return average;
    }

    public double getLimitOfDetection() {
        return limitOfDetection;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }
    
    

    public boolean usedMinSpikedInConcentration() {
        return usedMinSpikedInConcentration;
    }
    
    public boolean isZeroValueFlagged() {
        return zeroValueFlag;
    } 
    
    
}
