/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aero2.android.DefaultActivities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.aero2.android.DefaultActivities.Data.AirAzureContract;
import com.aero2.android.DefaultActivities.Data.AirAzureDbHelper;
import com.aero2.android.DefaultActivities.Data.AirAzureDownloadService;
import com.aero2.android.DefaultClasses.GPSTracker;
import com.aero2.android.DefaultClasses.Integrator;
import com.aero2.android.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * This shows how to draw polygons on a map.
 */
public class SmogMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private final String LOG_TAG = "SmogMapActivity";
    //************Variables related to Smog Map***************************************

    public GoogleMap googleMap;

    //**********Variables related to date*********************************************
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private static final int MAXIMUM_SMOG_VALUE = 1024;
    private static final int MY_PERMISSIONS_REQUEST = 2;
    private static final Double horizontalInt = 0.5;
    private static final Double verticalInt = 0.5;

    //UI ELEMENTS
    private FloatingActionButton legendButton;
    private FloatingActionButton recordActivityButtom;
    Double currLat, currLong;

    //Status check booleans
    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    private boolean ALARM_NOT_CALLED;
    public static boolean SERVICE_COMPLRETED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smog_map);
        Log.v("Smog Map", "Entered the smog map activity");
        legendButton = (FloatingActionButton) findViewById(R.id.legend);
        recordActivityButtom = (FloatingActionButton) findViewById(R.id.record);

        legendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add code for showing legend dialog here

                final AlertDialog.Builder builder = new AlertDialog.Builder(SmogMapActivity.this);
                builder.setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    }
                });
                builder.setTitle("LEGEND AERO2 MAP");
                builder.setView(R.layout.legend_layout);
                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        recordActivityButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recordActivityIntent = new Intent(getApplicationContext(), SmogRecordActivity.class);
                startActivity(recordActivityIntent);
            }
        });

        //START// New code that need to be verified if it works!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //END// New code that need to be verified if it works!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


        //          gps=new GPSTracker(getApplicationContext());
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //getLoaderManager().initLoader(AZURE_LOADER, null, this);

    }

    //****************************************************************************************************
    //*******************  ALL MAP RELATED WORK IS DONE IN THIS FUNCTION  ********************************
    //****************************************************************************************************
    @Override
    public void onMapReady(GoogleMap map) {

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        map.setContentDescription("Google Map with polygons.");

        //Retrieving the last known location from cache
        SharedPreferences latSharedPref = getApplicationContext().getSharedPreferences("LatitudeAerO2", Context.MODE_PRIVATE);
        SharedPreferences longSharedPref = getApplicationContext().getSharedPreferences("LongitudeAerO2", Context.MODE_PRIVATE);
        currLat = Double.valueOf(latSharedPref.getString("LatitudeAerO2", "33"));
        currLong = Double.valueOf(longSharedPref.getString("LongitudeAerO2", "72"));
        googleMap = map;
        //Moving camera to last known location
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currLat, currLong), 16));
        map.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        //add heat map to the instance of google map
        addHeatMap(map);


    }

    //Gets WeightedLatLong list (weighted with air index values) for the heatmap
    public List<WeightedLatLng> getListForHeatMap(Cursor cursor) {
        Log.v(LOG_TAG,"Entered the getListFotHeatMap Fuction");
        List<WeightedLatLng> list = new ArrayList<WeightedLatLng>();
        WeightedLatLng[] weightedLatLng = new WeightedLatLng[cursor.getCount()];
        Log.v("CusorLength:", " cursor length is " + cursor.getCount());
        if (cursor.moveToFirst()) {

            for (int i = 0; i < cursor.getCount(); i++) {

                cursor.moveToPosition(i);
                weightedLatLng[i] = new WeightedLatLng(new LatLng(Double.valueOf(cursor.getString(1)),
                        Double.valueOf(cursor.getString(2))),
                        Double.valueOf(cursor.getString(0)));
                //Log.v("RandomValue", "random smog value: " + random);
                Log.v("Cursor", cursor.getString(0) + " " + cursor.getString(1) + " " + cursor.getString(2));
                list.add(weightedLatLng[i]);

            }
        }
        return list;
    }


    public boolean addHeatMap(GoogleMap map){
        Log.v(LOG_TAG,"Entered the addHeatMapFunction");
        AirAzureDbHelper airAzureDbHelper = new AirAzureDbHelper(getApplicationContext());
        final SQLiteDatabase db = airAzureDbHelper.getReadableDatabase();
        //Using the simple cursor loader class to query the cache on a background thread
        SimpleCursorLoader simpleCursorLoader = new SimpleCursorLoader(getApplicationContext()) {
            @Override
            public Cursor loadInBackground() {


                //All this work is done in the background thread


                String[] columns = new String[]{
                        AirAzureContract.AirAzureEntry.COLUMN_AIR_INDEX,
                        AirAzureContract.AirAzureEntry.COLUMN_LAT,
                        AirAzureContract.AirAzureEntry.COLUMN_LONG
                };
                Cursor mCursor;
                mCursor = db.query(AirAzureContract.AirAzureEntry.TABLE_NAME, columns, null, null, null, null, null);
                return mCursor;

            }
        };


        //Getting a cursor containing the map data from the results cache
        Cursor cursor;
        cursor = simpleCursorLoader.loadInBackground();


        //******************************** Setting up the overlay on the Map Using the cursor *************************************
        //   STARTS HERE

        // Create the gradient.
        int[] colors = {
                Color.rgb(0, 255, 0),   // green
                Color.rgb(255, 255, 0),   // yellow
                Color.rgb(220, 0, 0)    // red
        };

        float[] startPoints = {
                0f, 0.5f, 1f
        };

        Gradient gradient = new Gradient(colors, startPoints);

        Log.v("CusorLength:", " cursor length is " + cursor.getCount());
        if (cursor.getCount() > 0) {
            //Make a Weighted heatmap of the Smog
            HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                    .weightedData(getListForHeatMap(cursor))
                    .gradient(gradient)
                    .opacity(0.3)
                    .radius(10)
                    .build();
            map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
            db.close();
            cursor.close();
            return true;
        }else {
            db.close();
            cursor.close();
            return false;
        }
    }

    @Override
    protected void onStart() {
        Log.v("onStart", "Entered the onStart Function of the Smog Map Activity");
        mGoogleApiClient.connect();
        SharedPreferences alarmPref = getApplicationContext().getSharedPreferences("ALARM_NOT_CALLED", Context.MODE_PRIVATE);
        ALARM_NOT_CALLED = alarmPref.getBoolean("ALARM_NOT_CALLED", true);
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v(LOG_TAG, "Entered the onConnected method");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return;
        }


        Log.v(LOG_TAG, "About to request Location");
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.v(LOG_TAG, "Requested Location");


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        currLat = location.getLatitude();
        currLong = location.getLongitude();

        /*
           storing the latest accurate know location in cache so that if location
           services are uavailable the last known location can be used.
        */
        SharedPreferences latSharedPref = getApplicationContext().getSharedPreferences("LatitudeAerO2", Context.MODE_PRIVATE);
        SharedPreferences longSharedPref = getApplicationContext().getSharedPreferences("LongitudeAerO2", Context.MODE_PRIVATE);
        SharedPreferences.Editor latEdit = latSharedPref.edit();
        latEdit.putString("LatitudeAerO2", String.valueOf(currLat));
        latEdit.commit();
        SharedPreferences.Editor longEdit = longSharedPref.edit();
        longEdit.putString("LongitudeAerO2", String.valueOf(currLong));
        longEdit.commit();

        Log.v(LOG_TAG, " " + currLat + " " + currLong);

        //if the Alarm hasn't been called once since the install of the app
        if (ALARM_NOT_CALLED) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currLat,currLong)));
            Intent alarmIntent = new Intent(SmogMapActivity.this, AirAzureDownloadService.AlarmReceiver.class);
            alarmIntent.putExtra(AirAzureDownloadService.CURRENT_LATITUDE, String.valueOf(currLat));
            alarmIntent.putExtra(AirAzureDownloadService.CURRENT_LONGITUDE, String.valueOf(currLong));
            alarmIntent.putExtra(AirAzureDownloadService.VERTICAL_INTERVAL, "8");
            alarmIntent.putExtra(AirAzureDownloadService.HORIZONTAL_INTERVAL, "8");
            alarmIntent.setAction(AirAzureDownloadService.DOWNLOAD_AZURE_AIR_DATA);
            //Wrap in a pending intent which only fires once.
            Log.v("OnCreate", "Alarm Intent Created");
            PendingIntent pi = PendingIntent.getBroadcast(SmogMapActivity.this, 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);//getBroadcast(context, 0, i, 0);

            AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

            Log.v("OnCreate", "AlarmManager Created");
            //Set the AlarmManager to wake up the system.
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, AlarmManager.INTERVAL_DAY, pi);
            SharedPreferences alarmPref = getApplicationContext().getSharedPreferences("ALARM_NOT_CALLED", Context.MODE_PRIVATE);
            SharedPreferences.Editor alarmPrefEditor = alarmPref.edit();
            alarmPrefEditor.putBoolean("ALARM_NOT_CALLED", false);
            alarmPrefEditor.commit();
            ALARM_NOT_CALLED = false;
        }

        //retrieving the value of service completion status
        SharedPreferences serviceStatus=getApplicationContext().getSharedPreferences("SERVICE_COMPLETED",Context.MODE_WORLD_WRITEABLE);
        SERVICE_COMPLRETED=serviceStatus.getBoolean("SERVICE_COMPLETED",false);

        // has the service been completed ??
        if(SERVICE_COMPLRETED){
            SharedPreferences.Editor serviceStatusEdit=serviceStatus.edit();
            serviceStatusEdit.putBoolean("SERVICE_COMPLETED",false);
            serviceStatusEdit.commit();

            //if the smog data download service has just completed restart the activity so that the refreshed map can be viewed
            Intent intent=new Intent(getApplicationContext(),SmogMapActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Permission Granted YAY!
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // this scope is unreachable as the permission has been granted if we have entered the parent scope.
                        return;
                    }

                    //once the permission to access fine location is granted restart the activity so that we can get the user's location
                    Intent intent=new Intent(getApplicationContext(),SmogMapActivity.class);
                    startActivity(intent);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }

    }

}