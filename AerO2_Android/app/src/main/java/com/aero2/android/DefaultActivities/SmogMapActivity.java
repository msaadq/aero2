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

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.aero2.android.DefaultActivities.Data.AirAzureContract;
import com.aero2.android.DefaultActivities.Data.AirAzureDbHelper;
import com.aero2.android.DefaultActivities.Data.AirAzureDownloadService;
import com.aero2.android.DefaultClasses.GPSTracker;
import com.aero2.android.DefaultClasses.Integrator;
import com.aero2.android.R;
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
public class SmogMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    //************Variables related to Smog Map***************************************

    private static LatLng CURRENT_LAT_LONG;
    private static final int MAXIMUM_SMOG_VALUE = 1024;

    private static final Double horizontalInt = 0.5;
    private static final Double verticalInt = 0.5;

    //UI ELEMENTS
    private FloatingActionButton legendButton;
    private FloatingActionButton recordActivityButtom;
    Double currLat, currLong;

    //Status check booleans
    private boolean gps_enabled = false;
    private boolean network_enabled = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smog_map);
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



        Integrator integrator = new Integrator(SmogMapActivity.this);
        Double[] newLocation;

        boolean valid;

        //Wait until gps values are correct
        do {
            newLocation = integrator.getLocation();
            currLat = newLocation[0];
            currLong = newLocation[1];

            if (currLat == null || currLong == null) {
                valid = false;
            }
            else{
                valid = true;
            }

        } while(!valid);

        Intent alarmIntent = new Intent(SmogMapActivity.this, AirAzureDownloadService.AlarmReceiver.class);
        alarmIntent.putExtra(AirAzureDownloadService.CURRENT_LATITUDE, String.valueOf(33.735));
        alarmIntent.putExtra(AirAzureDownloadService.CURRENT_LONGITUDE, String.valueOf(currLat));
        alarmIntent.putExtra(AirAzureDownloadService.VERTICAL_INTERVAL, "4");
        alarmIntent.putExtra(AirAzureDownloadService.HORIZONTAL_INTERVAL, "4");
        alarmIntent.setAction(AirAzureDownloadService.DOWNLOAD_AZURE_AIR_DATA);
        //Wrap in a pending intent which only fires once.
        Log.v("OnCreate", "Alarm Intent Created");
        PendingIntent pi = PendingIntent.getBroadcast(SmogMapActivity.this, 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);//getBroadcast(context, 0, i, 0);

        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        Log.v("OnCreate", "AlarmManager Created");
        //Set the AlarmManager to wake up the system.
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10, pi);



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

        //MAP CAMERA
        //TODO: Ulti values of lat and long
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currLong, currLat), 17));
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        //Using the simple cursor loader class to query the cache on a background thread
        SimpleCursorLoader simpleCursorLoader=new SimpleCursorLoader(getApplicationContext()) {
            @Override
            public Cursor loadInBackground() {

                //Equivalent longitude & latitude of distance equal to 20m
                final double longInterval = 0.000215901261691 ;
                final double latInterval = 0.000179807875453;

                //Compute corner points
                //TODO: Change the inverted values of lat and long
                double longRight = currLat + (horizontalInt/0.02)*longInterval;
                Log.v("Value: "," "+longRight);
                double longLeft = currLat - (horizontalInt/0.02)*longInterval;
                Log.v("Value: "," "+longLeft);
                double latTop = currLong + (verticalInt/0.02)*latInterval;
                Log.v("Value: "," "+latTop);
                double latBottom = currLong - (verticalInt/0.02)*latInterval;
                Log.v("Value: "," "+latBottom);

                //All this work is done in the background thread
                AirAzureDbHelper airAzureDbHelper=new AirAzureDbHelper(getApplicationContext());
                SQLiteDatabase db=airAzureDbHelper.getReadableDatabase();

                String[] columns=new String[]{
                        AirAzureContract.AirAzureEntry.COLUMN_AIR_INDEX,
                        AirAzureContract.AirAzureEntry.COLUMN_LAT,
                        AirAzureContract.AirAzureEntry.COLUMN_LONG
                };

                String[] selectionArgs = {String.valueOf(latTop),String.valueOf(latBottom),
                        String.valueOf(longRight),String.valueOf(longLeft)};

                String selection = AirAzureContract.AirAzureEntry.COLUMN_LAT + " < ? AND" +
                        AirAzureContract.AirAzureEntry.COLUMN_LAT + " > ? AND" +
                        AirAzureContract.AirAzureEntry.COLUMN_LONG + " < ? AND" +
                        AirAzureContract.AirAzureEntry.COLUMN_LONG + " > ?";

                Cursor mCursor;
                mCursor=db.query(AirAzureContract.AirAzureEntry.TABLE_NAME,columns,null,null,null,null,null);
                return mCursor;
            }
        };



        //Getting a cursor containing the map data from the results cache
        Cursor cursor;
        cursor=simpleCursorLoader.loadInBackground();





        //******************************** Setting up the overlay on the Map Using the cursor *************************************
        //   STARTS HERE

        // Create the gradient.
        int[] colors = {
                Color.rgb(0, 255, 0),   // green
                Color.rgb(255,255,0),   // yellow
                Color.rgb(220, 0, 0)    // red
        };

        float[] startPoints = {
                0f, 0.5f,1f
        };

        Gradient gradient = new Gradient(colors, startPoints);


        if(cursor.getCount()>0) {
            //Make a Weighted heatmap of the Smog
            HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                    .weightedData(getListForHeatMap(cursor))
                    .gradient(gradient)
                    .opacity(0.3)
                    .radius(10)
                    .build();
            map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        }

        //ENDS HERE
        //******************************** Setting up the overlay on the Map using the cursor *************************************



        //add a marker at the place the user is standing.
        //when touched it shows the smog value at the current position.
  /*
        MarkerOptions markerOptions=new MarkerOptions().draggable(false)
                .flat(true)
                .position(CURRENT_LAT_LONG)
                .title("Smog: 225")
                .alpha(5);
        map.addMarker(markerOptions);*/
    }

    //Gets WeightedLatLong list (weighted with air index values) for the heatmap
    public List<WeightedLatLng> getListForHeatMap(Cursor cursor){
        List<WeightedLatLng> list = new ArrayList<WeightedLatLng>();
        WeightedLatLng[] weightedLatLng=new WeightedLatLng[cursor.getCount()];
        Log.v("CusorLength:"," cursor length is "+cursor.getCount());
        if(cursor.moveToFirst()) {

            for (int i = 0; i < cursor.getCount(); i++) {

                cursor.moveToPosition(i);
                double random = Double.valueOf(cursor.getString(0));
                weightedLatLng[i] = new WeightedLatLng(new LatLng(Double.valueOf(cursor.getString(1)), Double.valueOf(cursor.getString(2))), random);
                //Log.v("RandomValue", "random smog value: " + random);
                list.add(weightedLatLng[i]);

            }
        }
        return list;
    }
}