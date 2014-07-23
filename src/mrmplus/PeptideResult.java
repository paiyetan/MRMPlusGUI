/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus;

import java.util.HashMap;
import java.util.LinkedList;
import mrmplus.statistics.resultobjects.*;

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
    //private LinkedList<Linearity> curveFits;
        
    //Updated outputs
    private String transitionID;
    private LimitOfDetection limitOfDetection;
    private LowerLimitOfQuantification lowerLimitOfQuantification;
    private Linearity linearity;
    private CarryOver carryOver;
    private PartialValidationOfSpecificity pvspecificity;
    private UpperLimitOfQuantification upperLimitOfQuantification;
               
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

    public String getPeptideSequence() {
        return peptideSequence;
    }

    public HashMap<String, String> getConfig() {
        return config;
    }

    public String getTransitionID() {
        return transitionID;
    }
    
    public LinkedList<PeptideRecord> getMappedRecords() {
        return mappedRecords;
    }

    public void setTransitionID(String transitionID) {
        this.transitionID = transitionID;
    }
    
    public void setLimitOfDetection(LimitOfDetection limitOfDetection) {
        this.limitOfDetection = limitOfDetection;
    }

    public void setLowerLimitOfQuantification(LowerLimitOfQuantification lowerLimitOfQuantification) {
        this.lowerLimitOfQuantification = lowerLimitOfQuantification;
    }

    public void setLinearity(Linearity peptideLinearity) {
        this.linearity = peptideLinearity;
    }
    
    public void setCarryOver(CarryOver carryOver) {
        this.carryOver = carryOver;
    }
    
    public void setPartialValidationOfSpecificity(PartialValidationOfSpecificity pvspecificity) {
        this.pvspecificity = pvspecificity;
    }
    
    public void setUpperLimitOfQuantification(UpperLimitOfQuantification uloq) {
        this.upperLimitOfQuantification = uloq;
    }
        
    public LimitOfDetection getLimitOfDetection() {
        return limitOfDetection;
    }

    public LowerLimitOfQuantification getLowerLimitOfQuantification() {
        return lowerLimitOfQuantification;
    }

    public Linearity getLinearity() {
        return linearity;
    }

    public CarryOver getCarryOver() {
        return carryOver;
    }

    public PartialValidationOfSpecificity getPartialValidationOfSpecificity() {
        return pvspecificity;
    }

    public UpperLimitOfQuantification getUpperLimitOfQuantification() {
        return upperLimitOfQuantification;
    }
 
    
}
