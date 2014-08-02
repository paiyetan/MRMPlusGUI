/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.resultobjects.experimentii;

/**
 *
 * @author paiyeta1
 */
public class ExpIIPartialValidationDeviation {
    
    private double mean;
    private double deviation;
    private String day;
    private String concentrationLevel;

    public ExpIIPartialValidationDeviation(double mean, double deviation) {
        this.mean = mean;
        this.deviation = deviation;
    }

    public double getDeviation() {
        return deviation;
    }

    public double getMean() {
        return mean;
    }
    
    public void setDay(String day){
        this.day = day;
    }
    
    public void setConcentrationLevel(String concLevel){
        this.concentrationLevel = concLevel;
    }

    public String getConcentrationLevel() {
        return concentrationLevel;
    }

    public String getDay() {
        return day;
    }
    
    
    
    
    
}
