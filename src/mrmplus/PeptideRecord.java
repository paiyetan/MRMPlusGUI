/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus;

/**
 *
 * @author paiyeta1
 */
public class PeptideRecord {
    
    //private 
    
    /*
     * [Local-Default] File header (Peptide record attributes):
        (1) PeptideSequence	
        (2) ReplicateName	
        (3) PrecursorCharge	
        (4) ProductCharge	
        (5) FragmentIon	
        (6) light PrecursorMz	
        (7) light ProductMz	
        (8) light RetentionTime	
        (9) light Area	
        (10) heavy PrecursorMz	
        (11) heavy ProductMz	
        (12) heavy RetentionTime	
        (13) heavy Area
     * 
     * 
     */
    //initiation values(s)
    private String PeptideSequence;	
    private String ReplicateName;	
    private int PrecursorCharge;	
    private int ProductCharge;	
    private String FragmentIon;	
    private double lightPrecursorMz;	
    private double lightProductMz;	
    private double lightRetentionTime;	
    private double lightArea;	
    private double heavyPrecursorMz;	
    private double heavyProductMz;	
    private double heavyRetentionTime;	
    private double heavyArea;
    
    //derived value(s)
    private double peakAreaRatio;
    
    //values from metadata file
    private String RunOrder;	
    private String AnalyteType;	
    private String Replicate;	
    private String CalibrationPoint;
    
    //values from dilution file.
    private double dilution = 0; // typically equivalent to the spikedInConcenteration
                                 // should the peptide record not be a blank, it'll be updated by the setDilution method call.

    public PeptideRecord(String PeptideSequence, 
                            String ReplicateName, 
                            int PrecursorCharge, 
                            int ProductCharge, 
                            String FragmentIon, 
                            double lightPrecursorMz, 
                            double lightProductMz, 
                            double lightRetentionTime, 
                            double lightArea, 
                            double heavyPrecursorMz, 
                            double heavyProductMz, 
                            double heavyRetentionTime, 
                            double heavyArea) {
        this.PeptideSequence = PeptideSequence;
        this.ReplicateName = ReplicateName;
        this.PrecursorCharge = PrecursorCharge;
        this.ProductCharge = ProductCharge;
        this.FragmentIon = FragmentIon;
        this.lightPrecursorMz = lightPrecursorMz;
        this.lightProductMz = lightProductMz;
        this.lightRetentionTime = lightRetentionTime;
        this.lightArea = lightArea;
        this.heavyPrecursorMz = heavyPrecursorMz;
        this.heavyProductMz = heavyProductMz;
        this.heavyRetentionTime = heavyRetentionTime;
        this.heavyArea = heavyArea;
        
        setPeakAreaRatio();
    }

    public String getFragmentIon() {
        return FragmentIon;
    }

    public String getPeptideSequence() {
        return PeptideSequence;
    }

    public int getPrecursorCharge() {
        return PrecursorCharge;
    }

    public int getProductCharge() {
        return ProductCharge;
    }

    public String getReplicateName() {
        return ReplicateName;
    }

    public double getHeavyArea() {
        return heavyArea;
    }

    public double getHeavyPrecursorMz() {
        return heavyPrecursorMz;
    }

    public double getHeavyProductMz() {
        return heavyProductMz;
    }

    public double getHeavyRetentionTime() {
        return heavyRetentionTime;
    }

    public double getLightArea() {
        return lightArea;
    }

    public double getLightPrecursorMz() {
        return lightPrecursorMz;
    }

    public double getLightProductMz() {
        return lightProductMz;
    }

    public double getLightRetentionTime() {
        return lightRetentionTime;
    }
    
    
    public boolean isBlank(){
        boolean isBlank = false;
        String[] strArr = ReplicateName.split("_");
        if(strArr[0].equalsIgnoreCase("Blank"))
            isBlank = true;       
        return isBlank;
    }
    
    public int getReplicateNumber(){
        int replicateNumber = 0;
        String[] strArr = ReplicateName.split("_");
        replicateNumber = Integer.parseInt(strArr[1]);            
        return replicateNumber;
    }

    private void setPeakAreaRatio() {
        //throw new UnsupportedOperationException("Not yet implemented");
        this.peakAreaRatio = this.heavyArea /this.lightArea;
    }
    
    public double getPeakAreaRatio(){
        return this.peakAreaRatio;
    }
    
    public double getMeasuredConcentration(double spikedInConcentration){
        double measuredConc = this.getPeakAreaRatio() * spikedInConcentration;
        return measuredConc;
    }
    
    //alternately 
    public double getMeasuredConcentration(){
        //peak ratio * spikedInConcetration...
        double measuredConc = peakAreaRatio * dilution;
        return measuredConc;       
    }

    // set from metadata info....
    public void setAnalyteType(String AnalyteType) {
        this.AnalyteType = AnalyteType;
    }

    public void setCalibrationPoint(String CalibrationPoint) {
        this.CalibrationPoint = CalibrationPoint;
    }

    public void setReplicate(String Replicate) {
        this.Replicate = Replicate;
    }

    public void setRunOrder(String RunOrder) {
        this.RunOrder = RunOrder;
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

    public String getRunOrder() {
        return RunOrder;
    }
    
    // from dilution file
    public void setDilution(double dilution) {
        this.dilution = dilution;
    }

    public double getDilution() {
        return dilution;
    }
    
    
    
    
    
    
    
}
