/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.resultobjects;

/**
 *
 * @author paiyeta1
 */
public class CoefficientOfVariation implements Comparable{
    
    //private String id;
    private double mean;
    private double sd;
    private double coef;
    
    private double dilutionConcentration;
    private String calibrationPoint;
    private int calibrationPointIndex;
    

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
        setCalibrationPointIndex();
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
        setCalibrationPointIndex();
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

    private void setCalibrationPointIndex() {
        //throw new UnsupportedOperationException("Not yet implemented");
        this.calibrationPointIndex = Integer.parseInt(this.calibrationPoint.split("_")[1]);
    }
    
    public int getCalibrationPointIndex(){
        return calibrationPointIndex;
    }

    @Override
    public int compareTo(Object o) {
        //throw new UnsupportedOperationException("Not supported yet.");
        int comparison = 0;
        CoefficientOfVariation objCV = (CoefficientOfVariation) o;
        int objCaliPointIndex = objCV.calibrationPointIndex;
        //double objMean = objCV.getMean();
        
        if(this.calibrationPointIndex < objCaliPointIndex){
        //if(this.mean < objMean){
            comparison = -1;
        } else if(this.calibrationPointIndex > objCaliPointIndex){
        //} else if(this.mean > objMean){
              comparison = 1;
        }       
        return comparison;
    }
    
    
    
}
