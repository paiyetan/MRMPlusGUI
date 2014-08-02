/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.resultobjects.experimentii;

import java.util.HashMap;

/**
 *
 * @author paiyeta1
 */
public class ExpIIValidationIdAndConcLevelsCoefs {
    
    private String transitionId;
    private HashMap<String, ExpIIConcLevelCoefficientOfVariation> concLevelToCoeffficientsMap;

    public ExpIIValidationIdAndConcLevelsCoefs(String transitionId, 
                        HashMap<String, ExpIIConcLevelCoefficientOfVariation> concLevelToCoeffficientsMap) {
        this.transitionId = transitionId;
        this.concLevelToCoeffficientsMap = concLevelToCoeffficientsMap;
    }

    public HashMap<String, ExpIIConcLevelCoefficientOfVariation> getConcLevelToCoeffficientsMap() {
        return concLevelToCoeffficientsMap;
    }

    public String getTransitionId() {
        return transitionId;
    }  
    
}
