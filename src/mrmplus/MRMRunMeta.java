/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus;

/**
 *
 * @author paiyeta1
 * 
 * Class describes associated information for each MRMExperiment,
 * Each experiment's
 *   - RunOrder	
 *   - ReplicateName	
 *   - AnalyteType	
 *   - Replicate, and	
 *   - CalibrationPoint
 */
public class MRMRunMeta {
    
    private String RunOrder;	
    private String ReplicateName;	
    private String AnalyteType;	
    private String Replicate;	
    private String CalibrationPoint;

    public MRMRunMeta(String RunOrder, String ReplicateName, String AnalyteType, String Replicate, String CalibrationPoint) {
        this.RunOrder = RunOrder;
        this.ReplicateName = ReplicateName;
        this.AnalyteType = AnalyteType;
        this.Replicate = Replicate;
        this.CalibrationPoint = CalibrationPoint;
    }

    public String getAnalyteType() {
        return AnalyteType;
    }

    public String getCalibrationPoint() {
        return CalibrationPoint;
    }

    public String getReplicate() {
        return Replicate;
    }

    public String getReplicateName() {
        return ReplicateName;
    }

    public String getRunOrder() {
        return RunOrder;
    }
    
    
    
    
}
