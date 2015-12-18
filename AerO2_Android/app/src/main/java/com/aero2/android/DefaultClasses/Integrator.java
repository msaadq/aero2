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
        gps = new GPSTracker(activity);
        //sensor = new STMCommunicator(activity);
        //sensor.bypassAuthentication();
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

        double [] integrated = new double[6];
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyMMdd.HHmmss");//dd/MM/yyyy
        Date date = new Date();
        String dateTime = sdfDate.format(date);


        Log.v("Status","Entering integrator try");
            /*
            sensor.authenticate("username","password");
            */
        double smog = 5.0;
        Log.v("Status","Done with BT Service");
        int airQuality = 98;
        double[] newLocation = gps.getGps();

        integrated[0] = Double.parseDouble(dateTime);
        integrated[1] = newLocation[0];
        integrated[2] = newLocation[1];
        integrated[3] = newLocation[2];
        integrated[4] = smog;

        Log.v("Hello", integrated[0] + " " + integrated[1] + " " + integrated[2] + " "
                + integrated[3] + " " + integrated[4] + " " + integrated[5]);

        return integrated;
    }


}
