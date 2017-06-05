/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Time class, this class is designed to provide time functionlity to hold
 * information about time.
 * @author Dennis, Alex, Niels and Tim.
 */
public class Time implements Serializable{
    
    /**
     * Instance variables.
     */
    private Calendar  calender;
    
    /**
     * Constructor.
     * @param time - Time format HH:mm:ss
     */
    public Time(String time) {
        
        //Convert string format to date.
        SimpleDateFormat  format = new SimpleDateFormat("HH:mm:ss");
        
        //Get calender.
        calender = GregorianCalendar.getInstance();
        
        try {
            
            calender.setTime(format.parse(time));
        
        } catch (ParseException ex) {
           
            throw new MyTunesException("Failed to parse time format");
        }
    }
    
    /**
     * Gets the seconds in the time.
     * @return seconds.
     */
    public int getSeconds() {
        return calender.get(Calendar.SECOND);
    }
    
    /**
     * Gets the minuts in the time.
     * @return minuts.
     */
    public int getMinuts() {
        return calender.get(Calendar.MINUTE);
    }
    
    /**
     * Gets the hours in the time.
     * @return hours.
     */
    public int getHours(){
        return calender.get(Calendar.HOUR);
    }

    /**
     * Override the default to string method.
     * @return 
     */
    @Override
    public String toString() {
        String sec;
        String min;
        String hr;
        
        sec = secondsToString();
        min = minutsToString();
        hr = hoursToString();
        
        
        return hr + ":" + min + ":" + sec;
    }

    /**
     * Convert hours to string.
     * @return 
     */
    public String hoursToString() {
        String hr;
        if (getHours()<10){
            hr = "0" + String.valueOf(getHours());
        }else{
            hr = String.valueOf(getHours());
        }
        return hr;
    }

    /**
     * Minuts to string.
     * @return 
     */
    public String minutsToString() {
        String min;
        if (getMinuts()<10){
            min = "0" + String.valueOf(getMinuts());
        }else{
            min = String.valueOf(getMinuts());
        }
        return min;
    }

    /**
     * Converts seconds to string.
     * @return 
     */
    public String secondsToString() {
        String sec;
        if (getSeconds()<10){
            sec = "0" + String.valueOf(getSeconds());
        }else{
            sec = String.valueOf(getSeconds());
        }
        return sec;
    }
    
    
}
