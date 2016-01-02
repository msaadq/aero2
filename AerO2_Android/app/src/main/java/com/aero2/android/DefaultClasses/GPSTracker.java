package com.aero2.android.DefaultClasses;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;


/**
 * Determines the gps location after every 2 seconds and returns a 1-D array
 * that contains longitude, latitude, and altitude.
 *
 * USE CASE:
 *         - First initialize by passing on the activity.
 *         - Next, call getGps() method which returns longitude,
 *         latitude and altitude in form of a 1-d array.
 *
 * Created by Usman on 11/14/2015
 */

public class GPSTracker {

    private Context context;
    private Double locations [];
    private static boolean gpsEnabled;
    private static LocationManager locationManager;
    public static Boolean settingDialogShown = false;   //Indicates if the setting dialog has been
                                                        //shown to user

    /**
     * Initializes the constructor and create
     * settings dialog if GPS is not enabled.
     * arg: The Current Activity
     * exception: None
     * return: No return value.
     */

    public GPSTracker(Context context){

        this.context=context;
        locations = new Double [3];

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) context.getSystemService
                (Context.LOCATION_SERVICE);

        getGPSStatus();

        if (!gpsEnabled) {

            //If setting Dialog is not already shown, display it
            if (!settingDialogShown) {
                Log.v("Status", "GPS not enabled.");
                showSettingsAlert();
                settingDialogShown = true;
            }
        }
    }

    /**
     * Get the GPS coordinates: Latitude, Longitude & Altitude
     * arg: None
     * exception: IOException
     * return: Double array containing latitude, longitude
     *  and altitude (in order)
     */

    public Double[] getGps(){

        Location new_location = null;

        // Select GPS for getting data
        String locationProvider = LocationManager.GPS_PROVIDER;

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}

        };

        try {
            //Ask for permission on run-time
            if (ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED ){

                locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
                Log.v("Status: ", "Location Requested!");
                new_location = locationManager.getLastKnownLocation(locationProvider);

            }

        } catch (Exception e) {
            Log.e("GPS Exception", "Permission Denied");
        }

        if (new_location != null) {

            locations[0] = new_location.getLongitude();
            locations[1] = new_location.getLatitude();
            locations[2] = new_location.getAltitude();

        }
        return locations;
    }


    /**
     * Creates a setting dialog to enable GPS
     * arg: None
     * exception: None
     * return: None
     */

    private void showSettingsAlert(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");
        // Setting Dialog Message
        alertDialog.setMessage("Your GPS may not be enabled. Do you want to go to settings menu?");
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);

            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    /**
     * Returns GPS Status to indicate if it is enabled.
     * arg: None
     * exception: None
     * return: Boolean indicating GPS Status
     */

    public static boolean getGPSStatus(){

        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.v("GPSTracker", "GPS is on? " + String.valueOf(gpsEnabled));
        return gpsEnabled;
    }

    public static boolean getNetworkStatus(){
        gpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.v("Network","Network is on? "+String.valueOf(gpsEnabled));
        return gpsEnabled;
    }

}
