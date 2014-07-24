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
 * Prioritizes LODs based on the concentration point used for its estimation from 
 * PreCurveBlanks to maximum spiked-in concentration.
 * 
 */
public class DetectionLevelPriorityQueue {
    
    private LinkedList<LimitOfDetection> priorityQueue;
    
    public DetectionLevelPriorityQueue(){
        this.priorityQueue = new LinkedList<LimitOfDetection>();
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public DetectionLevelPriorityQueue(LimitOfDetection lod) {
        this.priorityQueue = new LinkedList<LimitOfDetection>();
        this.insert(lod);
    }
    
    public int size(){
        return priorityQueue.size();
    }
    
    public LimitOfDetection peek(){
        return priorityQueue.peek();
    }
    
    public LimitOfDetection pop(){
        return priorityQueue.pop();
    }
    
    public void insert(LimitOfDetection lod){
        priorityQueue.add(lod);
        Collections.sort(priorityQueue);
    }
    
    
}
