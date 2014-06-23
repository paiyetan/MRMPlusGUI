/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ios;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author paiyeta1
 */
public class ConfigurationFileReader {
    
    public HashMap<String, String> readConfig(String configFile) throws FileNotFoundException, IOException{
        HashMap<String, String> config = new HashMap<String, String>();
        BufferedReader reader = new BufferedReader(new FileReader(configFile));
        String line;
        while((line = reader.readLine())!=null){
            String[] lineArr = line.split("=");
            String key = lineArr[0];
            String value = lineArr[1];
            config.put(key, value);
        }
        return config;
    }
    
}
