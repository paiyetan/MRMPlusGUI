/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.resultobjects.experimentii;

/**
 *
 * @author paiyeta1
 */
public class ExpIIPeptidePartialValidationOfSpecificity {
    
    private String concLevelAtMaxDeviation;
    private String dayAtMaxDeviation;
    private double meanAtMaxDeviation;
    private double maxDeviation;

    public ExpIIPeptidePartialValidationOfSpecificity(String concLevelAtMaxDeviation, 
                            String dayAtMaxDeviation, double meanAtMaxDeviation, double maxDeviation) {
        this.concLevelAtMaxDeviation = concLevelAtMaxDeviation;
        this.dayAtMaxDeviation = dayAtMaxDeviation;
        this.meanAtMaxDeviation = meanAtMaxDeviation;
        this.maxDeviation = maxDeviation;
    }
    
    public ExpIIPeptidePartialValidationOfSpecificity(String concLevelAtMaxDeviation, 
                            String dayAtMaxDeviation, ExpIIPartialValidationDeviation deviation){
        this.concLevelAtMaxDeviation = concLevelAtMaxDeviation;
        this.dayAtMaxDeviation = dayAtMaxDeviation;
        this.meanAtMaxDeviation = deviation.getMean();
        this.maxDeviation = deviation.getDeviation();
    }
    
    public ExpIIPeptidePartialValidationOfSpecificity(ExpIIPartialValidationDeviation deviation){
        this.concLevelAtMaxDeviation = deviation.getConcentrationLevel();
        this.dayAtMaxDeviation = deviation.getDay();
        this.meanAtMaxDeviation = deviation.getMean();
        this.maxDeviation = deviation.getDeviation();
    }

    public String getConcLevelAtMaxDeviation() {
        return concLevelAtMaxDeviation;
    }

    public double getMaxDeviation() {
        return maxDeviation;
    }

    public double getMeanAtMaxDeviation() {
        return meanAtMaxDeviation;
    }

    public String getDayAtMaxDeviation() {
        return dayAtMaxDeviation;
    }
    
    
    
    
}
