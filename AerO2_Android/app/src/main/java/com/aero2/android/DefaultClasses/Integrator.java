package com.aero2.android.DefaultClasses;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.util.Log;

import com.aero2.android.DefaultActivities.MainActivity;

/**
 *
 * Instantiates STMCommunicator's and GPSTracker's objects and
 * integrates them.
 *
 * USE CASE:
 *        - First initialize by passing on the activity.
 *        - Next, call integrateSmog() method which will return all
 *        parameters in a 1-d array.
 *
 * Created by Usman on 11/17/2015.
 */

public class Integrator {

    STMCommunicator sensor;
    GPSTracker gps;

    // Authentication Strings
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    /**
     * Initializes the constructor by instantiating
     * GPSTracker & STMCommunicator Objects
     * arg: The Current Activity
     * exception: IOException
     * return: No return value.
     */

    public Integrator(Activity activity) {
        try {
            gps = new GPSTracker(activity);
            sensor = new STMCommunicator(activity);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Integrates Smog with GPS and time values.
     * arg: None
     * exception: IOException
     * return: Double array containing date&time,
     * longitude, latitude, altitude, smog and normalized
     * (in order)
     */

    public String[] integrateSmog() {

        String[] integrated = new String[6];
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyMMdd.HHmmss");   //dd/MM/yyyy
        Date date = new Date();
        String dateTime = sdfDate.format(date);

        try {

            sensor.authenticate("username", "password");
            String smog = sensor.getSmogValue();
            String[] newLocation = gps.getGps();

            integrated[0] = String.valueOf(dateTime);
            integrated[1] = newLocation[0];
            integrated[2] = newLocation[1];
            integrated[3] = newLocation[2];
            integrated[4] = smog;

        } catch (IOException ie) {
            ie.printStackTrace();
        }

        return integrated;
    }

}