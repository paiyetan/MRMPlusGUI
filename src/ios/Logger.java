/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ios;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.logging.Level;

/**
 *
 * @author paiyeta1
 */
public class Logger {
    
    private PrintWriter printer;
    private boolean verbose;
    
    public Logger(String logFile){
        try {
            printer = new PrintWriter(logFile);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
        }
        verbose = false;
    }
    
    public Logger(String logFile, boolean verbose){
        try {
            printer = new PrintWriter(logFile);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.verbose = verbose;
    }
    
    public void println(String statement){
        printer.println(statement);
        if(verbose){
            System.out.println(statement);
        }
    }
    
    public void print(String statement){
        printer.print(statement);
        if(verbose){
            System.out.print(statement);
        }
    }
    
    public void close(){
        try{
            printer.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
}
