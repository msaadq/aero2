package com.aero2.android.DefaultClasses;

import java.io.IOException;
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
            int smog = sensor.getSmogValue();
            Log.v("Value",String.valueOf(smog));


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

    public double[] integrateSmog(){

        double [] integrated = {};
        Date date = new Date();

        try {
            int smog = sensor.getSmogValue();
            double[] newLocation = gps.getGps();
            int temp = integrated.length;
            integrated = new double[temp + 2];

            System.arraycopy(smog, 0, integrated, 0, 1);
            System.arraycopy(newLocation,0,integrated,1,temp);
            System.arraycopy(date, 0, integrated, 1 + temp, 1);
        }
        catch (IOException ie){
            ie.printStackTrace();
        }

        return integrated;
    }


}
