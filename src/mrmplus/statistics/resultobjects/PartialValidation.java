/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.resultobjects;

/**
 *
 * @author paiyeta1
 */
public class PartialValidation {
    
    private String transition;
    private String calibrationPoint;
    private double caliPointAverage;
    private double caliPointMaxDeviation;

    public PartialValidation(String transition, 
                                String caliPoint, 
                                    double caliPointAverage, 
                                        double caliPointMaxDeviation) {
        //throw new UnsupportedOperationException("Not yet implemented");
        this.transition = transition;
        this.calibrationPoint = caliPoint;
        this.caliPointAverage = caliPointAverage;
        this.caliPointMaxDeviation = caliPointMaxDeviation;
    }

    public double getCaliPointAverage() {
        return caliPointAverage;
    }

    public double getCaliPointMaxDeviation() {
        return caliPointMaxDeviation;
    }

    public String getCalibrationPoint() {
        return calibrationPoint;
    }

    public String getTransition() {
        return transition;
    }    
    
}
