package com.example.usmankhan.gps;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;


/** Determines the gps location after every 2 seconds and returns a 1-D array
 * that contains longitude, latitude, and altitude.
 *
 * @author Usman Mahmood
 * @version 0.2
 */

public class GPSTracker {

    private static int value_count;
    // Maximum number of expected values
    private final int max_value_count = 1000;
    private Context context;
    private static double locations [];

    GPSTracker(Context context){

        this.context=context;
        value_count = 0;
        locations = new double [3];
    }

    /**
     * Acquires latitude, longitude and
     * altitude of user
     * arg: None
     * exception: Security Exception
     * return: Double array
     */
    public double[] getGps(){

        boolean GPS_enabled = false;
        Location new_location = null;

        // Select GPS for getting data
        String locationProvider = LocationManager.GPS_PROVIDER;

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) context.getSystemService
                (Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        GPS_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!GPS_enabled) {

            //If GPS is not enabled, finish taking values
            value_count = max_value_count;

        }

        if (GPS_enabled) {
            try {
                locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
                new_location = locationManager.getLastKnownLocation(locationProvider);

            } catch (Exception e) {
                Log.e("Security Exception", "Permission Denied");
            }

            if (new_location != null) {

                //Add parameters of new location to 1-d array
                locations[0] = new_location.getLongitude();
                locations[1] = new_location.getLatitude();
                locations[2] = new_location.getAltitude();
            }

        }
        value_count++;
        return locations;
    }

    /**
     * Creates a setting dialog and allow
     * users to edit their location settings.
     * arg: None
     * exception: None
     * return: No return value.
     */

    public void showSettingsAlert(){

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
     * Counts the total instances gps values have
     * been acquired.
     * arg: None
     * exception: None
     * return: Count in 'int'.
     */
    public int getValueCount(){
        return value_count;
    }

}
