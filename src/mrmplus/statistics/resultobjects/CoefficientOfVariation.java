/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.resultobjects;

/**
 *
 * @author paiyeta1
 */
public class CoefficientOfVariation {
    
    //private String id;
    private double mean; //mean of measuredConcentration.
    private double sd; // sd of measuredConcentration
    private double coef;
    
    private double dilutionConcentration;
    private String calibrationPoint;
    

    public CoefficientOfVariation(//String id, 
                                    double mean, double sd) {
        //this.id = id;
        this.mean = mean;
        this.sd = sd;
        setCoefficient();
    }

    public CoefficientOfVariation(//String id, 
                                    double mean, double sd, double coef) {
        //this.id = id;
        this.mean = mean;
        this.sd = sd;
        this.coef = coef;
    }

    public CoefficientOfVariation(//String id, 
                                    double mean, double sd, 
                                                double dilutionConcentration, String calibrationPoint) {
        //this.id = id;
        this.mean = mean;
        this.sd = sd;
        this.dilutionConcentration = dilutionConcentration;
        this.calibrationPoint = calibrationPoint;
        setCoefficient();
    }
    
    public CoefficientOfVariation(//String id, 
                                    double mean, double sd, double coef,
                                                    double dilutionConcentration, String calibrationPoint) {
        //this.id = id;
        this.mean = mean;
        this.sd = sd;
        this.coef = coef;
        this.dilutionConcentration = dilutionConcentration;
        this.calibrationPoint = calibrationPoint;
        //setCoefficient();
    }
    
    

    public double getCoef() {
        return coef;
    }

    //public String getId() {
    //    return id;
    //}

    public double getMean() {
        return mean;
    }

    public double getSd() {
        return sd;
    }

    private void setCoefficient() {
        //throw new UnsupportedOperationException("Not yet implemented");
        this.coef = (this.sd /this.mean) * 100;
    }

    public String getCalibrationPoint() {
        return calibrationPoint;
    }

    public double getDilutionConcentration() {
        return dilutionConcentration;
    }
    
    
    
}
