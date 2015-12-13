package com.aero2.android.DefaultClasses;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.util.Log;

/**
 *
 * Instantiates STMCommunicator's and GPSTracker's objects and
 * integrates them.
 *
 * Created by Usman on 11/17/2015.
 */
public class Integrator {

    STMCommunicator sensor;
    GPSTracker gps;

    private int value_count;
    // Maximum number of expected values
    private final int max_value_count = 10000;

    // Authentication Strings
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    public Integrator(Activity activity){
        try{

            sensor = new STMCommunicator(USERNAME, PASSWORD);
            gps = new GPSTracker(activity);
            for (int i=0;i<25000000;i++){

            }
            sensor.bypassAuthentication();
            //int smog = sensor.getSmogValue();
            //Log.v("Value",String.valueOf(smog));


        }
        catch (IOException ie){
            ie.printStackTrace();
        }
    }

    /**
     * Integrates Smog with GPS and time
     * values.
     * arg: None
     * exception: None
     * return: Double array containing smog,
     * longitude, latitude, altitude and time (in order)
     */

    public String[] integrateSmog(){

        String [] integrated = new String [6];
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyMMdd;HHmmss");//dd/MM/yyyy
        Date date = new Date();
        String dateTime = sdfDate.format(date);
        //try {
            //int smog = sensor.getSmogValue();
            int smog = 10;
            int airQuality = 98;
            double[] newLocation = gps.getGps();

            integrated[0] = String.valueOf(dateTime);
            integrated[1] = String.valueOf(newLocation[0]);
            integrated[2] = String.valueOf(newLocation[1]);
            integrated[3] = String.valueOf(newLocation[2]);
            integrated[4] = String.valueOf(smog);
            integrated[5] = String.valueOf(airQuality);

            Log.v("Hello",integrated[0]+" "+integrated[2]+" "+integrated[1]+" "+integrated[3]+" "
                    +integrated[4]+" "+integrated[5]);
        //}
        //catch (IOException ie){
          //  ie.printStackTrace();
        //}

        return integrated;
    }


}
