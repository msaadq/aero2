package com.aero2.android.DefaultClasses;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;

import com.aero2.android.DefaultActivities.MainActivity;

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

    /**
     * Initializes the constructor by instatiating
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
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyMMdd;HHmmss");   //dd/MM/yyyy
        Date date = new Date();
        String dateTime = sdfDate.format(date);

        try {

            //Authenticate for first time
            if (MainActivity.value_count == 1) {
                sensor.authenticate("username", "password");
            }

            int smog = sensor.getSmogValue();
            double[] newLocation = gps.getGps();

            integrated[0] = String.valueOf(dateTime);
            integrated[1] = String.valueOf(newLocation[0]);
            integrated[2] = String.valueOf(newLocation[1]);
            integrated[3] = String.valueOf(newLocation[2]);
            integrated[4] = String.valueOf(smog);
            integrated[5] = String.valueOf(false);
        } catch (IOException ie) {
            ie.printStackTrace();
        }

        return integrated;
    }

}