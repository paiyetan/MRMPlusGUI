/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus;

import java.util.HashMap;
import java.util.LinkedList;
import mrmplus.statistics.resultobjects.CurveFit;
import mrmplus.statistics.resultobjects.LimitOfDetection;
import mrmplus.statistics.resultobjects.LowerLimitOfQuantification;

/**
 *
 * @author paiyeta1
 */
public class PeptideResult {
    
    //inputs...
    private String peptideSequence;
    //private 
    private LinkedList<PeptideRecord> mappedRecords;
    private HashMap<String, String> config;
    
    //outputs...
    private LinkedList<CurveFit> curveFits;
    private LinkedList<LimitOfDetection> limitsOfDetections;
    private LinkedList<LowerLimitOfQuantification> lowerLimitsOfQuantifications;
        
    //Updated outputs
    private String transitionID;
    private CurveFit curveFit;
    private LimitOfDetection limitOfDetection;
    private LowerLimitOfQuantification lowerLimitOfQuantification;
               
    public PeptideResult(String peptideSequence, 
                    LinkedList<PeptideRecord> mappedRecords,
                        HashMap<String, String> config){
        this.peptideSequence = peptideSequence;
        this.mappedRecords = mappedRecords;
        this.config = config;

    }
    
    public PeptideResult(String peptideSequence){
        this.peptideSequence = peptideSequence;
    }

    public void setCurveFits(LinkedList<CurveFit> curveFits) {
        this.curveFits = curveFits;
    }

    public void setLimitsOfDetections(LinkedList<LimitOfDetection> limitsOfDetections) {
        this.limitsOfDetections = limitsOfDetections;
    }

    public void setLowerLimitsOfQuantifications(LinkedList<LowerLimitOfQuantification> lowerLimitsOfQuantifications) {
        this.lowerLimitsOfQuantifications = lowerLimitsOfQuantifications;
    }
    
    public String getPeptideSequence() {
        return peptideSequence;
    }

    public HashMap<String, String> getConfig() {
        return config;
    }

    public LinkedList<CurveFit> getCurveFits() {
        return curveFits;
    }

    public LinkedList<LimitOfDetection> getLimitsOfDetections() {
        return limitsOfDetections;
    }

    public LinkedList<LowerLimitOfQuantification> getLowerLimitsOfQuantifications() {
        return lowerLimitsOfQuantifications;
    }

    public LinkedList<PeptideRecord> getMappedRecords() {
        return mappedRecords;
    }

    public void setTransitionID(String transitionID) {
        this.transitionID = transitionID;
    }

    public void setCurveFit(CurveFit curveFit) {
        this.curveFit = curveFit;
    }

    public void setLimitOfDetection(LimitOfDetection limitOfDetection) {
        this.limitOfDetection = limitOfDetection;
    }

    public void setLowerLimitOfQuantification(LowerLimitOfQuantification lowerLimitOfQuantification) {
        this.lowerLimitOfQuantification = lowerLimitOfQuantification;
    }

    public CurveFit getCurveFit() {
        return curveFit;
    }

    public LimitOfDetection getLimitOfDetection() {
        return limitOfDetection;
    }

    public LowerLimitOfQuantification getLowerLimitOfQuantification() {
        return lowerLimitOfQuantification;
    }

    public String getTransitionID() {
        return transitionID;
    }
    
    
    
    

    
    
    
    
}
