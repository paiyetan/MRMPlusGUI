/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.resultobjects;

/**
 *
 * @author paiyeta1
 */
public class LimitOfDetection implements Comparable{
    
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
    private boolean usedSpikedInConcentration;
    private boolean zeroValueFlag;
    private String concentrationPointUsed;
    private int calibrationPointUsed;
    private boolean undetectable;

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
                                    double sd, double lod, boolean usedSpikedInConcentration){
        this.transitionID = transitionID;
        this.average = average;
        this.standardDeviation = sd;
        this.limitOfDetection = lod;
        this.usedSpikedInConcentration = usedSpikedInConcentration;       
    }
    
    public LimitOfDetection(double average, double sd, double lod){
        //this.transitionID = transitionID;
        this.average = average;
        this.standardDeviation = sd;
        this.limitOfDetection = lod;
        //this.usedSpikedInConcentration = usedSpikedInConcentration;       
    }
    
    public LimitOfDetection(double average, double sd, double lod, boolean zeroFlagged){
        //this.transitionID = transitionID;
        this.average = average;
        this.standardDeviation = sd;
        this.limitOfDetection = lod;
        this.zeroValueFlag = zeroFlagged;       
    }
    
    public void setUsedSpikedInConcentration(boolean use){
        usedSpikedInConcentration = use;
    }

    public void setTransitionID(String transitionID) {
        this.transitionID = transitionID;
    }
    
    public void setZeroValueFlag(boolean zeroValueFlag) {
        this.zeroValueFlag = zeroValueFlag;
    }

    public void setConcentrationPointUsed(String concentrationPointUsed) {
        this.concentrationPointUsed = concentrationPointUsed;
        setCalibrationPointUsed();
    }
    
    private void setCalibrationPointUsed() {
        if(this.concentrationPointUsed.equalsIgnoreCase("preCurveBlanks")){
            this.calibrationPointUsed = 0;
        
        } else if(concentrationPointUsed.equalsIgnoreCase("NA")) { //in which case it is undetectable
            this.calibrationPointUsed = Integer.MAX_VALUE;           
        
        } else {
            this.calibrationPointUsed = Integer.parseInt(concentrationPointUsed.split("_")[1]);
        }
    }
    
    public void setUnDetectable(boolean undetectable){
        this.undetectable = undetectable;
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

    public boolean usedSpikedInConcentration() {
        return usedSpikedInConcentration;
    }
    
    public boolean hasZeroValueFlagged() {
        return zeroValueFlag;
    }

    public String getConcentrationPointUsed() {
        return concentrationPointUsed;
    }

    public boolean isUndetectable(){
        return undetectable;
    }

    public int getCalibrationPointUsed() {
        return calibrationPointUsed;
    }

    @Override
    public int compareTo(Object lod) {
        //throw new UnsupportedOperationException("Not supported yet.");
        int classCalibrationPoint = calibrationPointUsed;
        LimitOfDetection objToCompare = (LimitOfDetection) lod;
        int objectsCalibrationPoint = objToCompare.getCalibrationPointUsed();
        
        int compareValue = 0;
        if(classCalibrationPoint < objectsCalibrationPoint){
            compareValue = -1;
        }
        if(classCalibrationPoint > objectsCalibrationPoint){
            compareValue = 1;
        }        
        return compareValue;
    }
    
}
