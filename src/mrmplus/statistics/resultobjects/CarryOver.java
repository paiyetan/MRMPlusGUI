/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.resultobjects;

/**
 *
 * @author paiyeta1
 */
public class CarryOver {
    
    private String transitionID; 
    private double postMaxBlankPeakArea; // averaged blank peak areas after max
    private double maxConcPointPeakArea; // averaged peak area at maximum dilution concentration...

    public CarryOver(String transitionID, double postMaxBlankPeakArea, double maxConcPointPeakArea) {
        this.transitionID = transitionID;
        this.postMaxBlankPeakArea = postMaxBlankPeakArea;
        this.maxConcPointPeakArea = maxConcPointPeakArea;
    }

    public double getMaxConcPointPeakArea() {
        return maxConcPointPeakArea;
    }

    public double getPostMaxBlankPeakArea() {
        return postMaxBlankPeakArea;
    }

    public String getTransitionID() {
        return transitionID;
    }
    
    public double getCarryOverValue(){
        double value = (postMaxBlankPeakArea/maxConcPointPeakArea) * 100;
        return value;
    }
    
}
