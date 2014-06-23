/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ios;

import java.io.*;
import java.util.HashMap;

/**
 *
 * @author paiyeta1
 */
public class DilutionFileReader {
    
    public HashMap<String,Double> readFile(String dilutionFile, PrintWriter logWriter) throws FileNotFoundException, IOException{
        HashMap<String,Double> dilution = new HashMap<String,Double>();
        BufferedReader input = new BufferedReader(new FileReader(new File(dilutionFile)));        

        int lines_read = 0;
        String line;
        System.out.println(" Reading experiment's dilution(s)...");
        logWriter.println(" Reading experiment's dilution(s)...");
        
        while ((line = input.readLine()) != null) {
            lines_read++;
            if (lines_read > 1){ //skipping header line
                String[] lineArr = line.split("\t");
                String dilution_point = lineArr[0];
                double dilution_amount = Double.parseDouble(lineArr[1]);
                dilution.put(dilution_point, dilution_amount);              
            }
        }
        
        logWriter.println("  " + dilution.keySet().size() + " dilution(s) found/performed");
        return dilution;
    }
    
    
    
}
