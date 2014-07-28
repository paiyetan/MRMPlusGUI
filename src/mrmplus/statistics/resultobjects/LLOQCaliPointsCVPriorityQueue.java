/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.resultobjects;

import java.util.Collections;
import java.util.LinkedList;

/**
 *
 * @author paiyeta1
 * 
 * Prioritizes LLOQ computed CVs less than pre-specified limit ( < 20% by default) 
 * based on the lowest [averaged] concentration of peptide
 * 
 * RECALL/NOTE: LLOQ is described as the lowest concentration of peptide at which the imprecision of the assay [expressed as
 * the coefficient of variation (CV), calculated as the standard deviation/average is < 20% 
 * 
 * 
 */
public class LLOQCaliPointsCVPriorityQueue {
    
    private LinkedList<CoefficientOfVariation> priorityQueue;
    
    public LLOQCaliPointsCVPriorityQueue(){
        this.priorityQueue = new LinkedList<CoefficientOfVariation>();
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public LLOQCaliPointsCVPriorityQueue(CoefficientOfVariation coef) {
        this.priorityQueue = new LinkedList<CoefficientOfVariation>();
        this.insert(coef);
    }
    
    public int size(){
        return priorityQueue.size();
    }
    
    public CoefficientOfVariation peek(){
        return priorityQueue.peek();
    }
    
    public CoefficientOfVariation pop(){
        return priorityQueue.pop();
    }
    
    public void insert(CoefficientOfVariation coef){
        priorityQueue.add(coef);
        Collections.sort(priorityQueue);
    }
    
}
