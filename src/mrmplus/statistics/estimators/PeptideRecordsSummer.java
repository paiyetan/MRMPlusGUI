/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mrmplus.statistics.estimators;

import java.util.LinkedList;
import mrmplus.PeptideRecord;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.summary.Sum;

/**
 *
 * @author paiyeta1
 */
public class PeptideRecordsSummer {

    /*
     * Sums the peak areas of given PeptideRecords..
     * @param peptideRecords
     * @return summedPeptideRecord
     */
    public PeptideRecord sumPeptideRecords(LinkedList<PeptideRecord> peptideRecords) {
        //throw new UnsupportedOperationException("Not yet implemented");
        PeptideRecord summedPeptideRecord = null;
        //initiation values(s)
        String PeptideSequence;	
        String ReplicateName;	
        int PrecursorCharge;	
        int ProductCharge;	
        String FragmentIon;	
        double lightPrecursorMz;	
        double lightProductMz;	
        double lightRetentionTime;	
        double lightArea;	
        double heavyPrecursorMz;	
        double heavyProductMz;	
        double heavyRetentionTime;	
        double heavyArea;

        /*
        //derived value(s)
        double peakAreaRatio;

        //values from metadata file
        String RunOrder;	
        String AnalyteType;	
        String Replicate;	
        String CalibrationPoint;

        //*
         * values from dilution file.
          typically equivalent to the spikedInConcenteration; 
          should the peptide record not be a blank, it'll be updated by the setDilution method call. 
          * NB: The dilution often is equivalent to the Calibration point.
          *
        double dilution = 0; 
        */
        PeptideSequence = peptideRecords.getFirst().getPeptideSequence();//it is expected that the peptide sequence is common	
        ReplicateName = "summedPeptideRecordsDerived";	
        PrecursorCharge = averagePrecursorCharges(peptideRecords);	
        ProductCharge = averageProductCharges(peptideRecords);	
        FragmentIon = "summedPeptideRecordsDerived";	
        lightPrecursorMz = averageLightPrecursorMz(peptideRecords);	
        lightProductMz = averageLightProductMz(peptideRecords);	
        lightRetentionTime = averageLightRTs(peptideRecords);	
        lightArea = sumLightAreas(peptideRecords);	
        heavyPrecursorMz = averageHeavyPrecursorMz(peptideRecords);	
        heavyProductMz = averageHeavyProductMz(peptideRecords);
        heavyRetentionTime = averageHeavyRTs(peptideRecords);	
        heavyArea = sumHeavyAreas(peptideRecords);
        
        summedPeptideRecord = new PeptideRecord(PeptideSequence,//it is expected that the peptide sequence is common	
                                                ReplicateName,	
                                                PrecursorCharge,	
                                                ProductCharge,	
                                                FragmentIon,	
                                                lightPrecursorMz,	
                                                lightProductMz,	
                                                lightRetentionTime,	
                                                lightArea,	
                                                heavyPrecursorMz,	
                                                heavyProductMz,
                                                heavyRetentionTime,	
                                                heavyArea);
        return summedPeptideRecord;
    }

    private int averagePrecursorCharges(LinkedList<PeptideRecord> peptideRecords) {
        //throw new UnsupportedOperationException("Not yet implemented");
        int average = 0;
        double[] values = new double[peptideRecords.size()];
        for(int i = 0; i < values.length; i++){
            values[i] = (double) peptideRecords.get(i).getPrecursorCharge();           
        }
        Mean mean = new Mean();
        average = (int) mean.evaluate(values);        
        return average;
    }

    private int averageProductCharges(LinkedList<PeptideRecord> peptideRecords) {
        int average = 0;
        double[] values = new double[peptideRecords.size()];
        for(int i = 0; i < values.length; i++){
            values[i] = (double) peptideRecords.get(i).getProductCharge();           
        }
        Mean mean = new Mean();
        average = (int) mean.evaluate(values);        
        return average;
    }

    private double averageLightPrecursorMz(LinkedList<PeptideRecord> peptideRecords) {
        double average = 0;
        double[] values = new double[peptideRecords.size()];
        for(int i = 0; i < values.length; i++){
            values[i] = peptideRecords.get(i).getLightPrecursorMz();           
        }
        Mean mean = new Mean();
        average = mean.evaluate(values);        
        return average;
    }

    private double averageLightProductMz(LinkedList<PeptideRecord> peptideRecords) {
        double average = 0;
        double[] values = new double[peptideRecords.size()];
        for(int i = 0; i < values.length; i++){
            values[i] = peptideRecords.get(i).getLightProductMz();           
        }
        Mean mean = new Mean();
        average = mean.evaluate(values);        
        return average;
    }

    private double averageLightRTs(LinkedList<PeptideRecord> peptideRecords) {
        double average = 0;
        double[] values = new double[peptideRecords.size()];
        for(int i = 0; i < values.length; i++){
            values[i] = peptideRecords.get(i).getLightRetentionTime();           
        }
        Mean mean = new Mean();
        average = mean.evaluate(values);        
        return average;
    }

    private double sumLightAreas(LinkedList<PeptideRecord> peptideRecords) {
        double summed = 0;
        double[] values = new double[peptideRecords.size()];
        for(int i = 0; i < values.length; i++){
            values[i] = peptideRecords.get(i).getLightArea();           
        }
        Sum sum = new Sum();
        summed = sum.evaluate(values);        
        return summed;
    }

    private double averageHeavyPrecursorMz(LinkedList<PeptideRecord> peptideRecords) {
        double average = 0;
        double[] values = new double[peptideRecords.size()];
        for(int i = 0; i < values.length; i++){
            values[i] = peptideRecords.get(i).getHeavyPrecursorMz();           
        }
        Mean mean = new Mean();
        average = mean.evaluate(values);        
        return average;
    }

    private double averageHeavyProductMz(LinkedList<PeptideRecord> peptideRecords) {
        double average = 0;
        double[] values = new double[peptideRecords.size()];
        for(int i = 0; i < values.length; i++){
            values[i] = peptideRecords.get(i).getHeavyProductMz();           
        }
        Mean mean = new Mean();
        average = mean.evaluate(values);        
        return average;
    }

    private double averageHeavyRTs(LinkedList<PeptideRecord> peptideRecords) {
        double average = 0;
        double[] values = new double[peptideRecords.size()];
        for(int i = 0; i < values.length; i++){
            values[i] = peptideRecords.get(i).getHeavyRetentionTime();           
        }
        Mean mean = new Mean();
        average = mean.evaluate(values);        
        return average;
    }

    private double sumHeavyAreas(LinkedList<PeptideRecord> peptideRecords) {
        double summed = 0;
        double[] values = new double[peptideRecords.size()];
        for(int i = 0; i < values.length; i++){
            values[i] = peptideRecords.get(i).getHeavyArea();           
        }
        Sum sum = new Sum();
        summed = sum.evaluate(values);        
        return summed;
    }
    
}
