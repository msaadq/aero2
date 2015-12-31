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
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.aero2.android.DefaultActivities.Data.AirAzureContract;
import com.aero2.android.DefaultActivities.Data.AirAzureDbHelper;
import com.aero2.android.DefaultActivities.Data.AirAzureDownloadService;
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
public class SmogMap extends AppCompatActivity implements OnMapReadyCallback,LoaderManager.LoaderCallbacks<Cursor>{

    private static GoogleMap googleMap;

    //************Variables related to Smog Map***************************************

    private static LatLng CURRENT_LAT_LONG;

    private static final int MAXIMUN_SMOG_VALUE=1024;

    private static String LONG_LEFT_LIM;

    private static String LONG_RIGHT_LIM;

    private static String LAT_TOP_LIM;

    private static String LAT_BOTTOM_LIM;

    //************Variables related to Smog Map***************************************


    private FloatingActionButton legendButton;

    private FloatingActionButton recordActivityButtom;

    private boolean gps_enabled=false;

    private boolean network_enabled=false;

    private static final int AZURE_LOADER = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smog_map);
        legendButton=(FloatingActionButton) findViewById(R.id.legend);
        recordActivityButtom=(FloatingActionButton) findViewById(R.id.record);

        legendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add code for showing legend dialog here

                final AlertDialog.Builder builder = new AlertDialog.Builder(SmogMap.this);
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
                Intent recordActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(recordActivityIntent);
            }
        });

        Intent alarmIntent = new Intent(SmogMap.this, AirAzureDownloadService.AlarmReceiver.class);
        alarmIntent.putExtra(AirAzureDownloadService.LONGITUDE_LIMIT_LEFT, "0");
        alarmIntent.putExtra(AirAzureDownloadService.LONGITUDE_LIMIT_RIGHT, "0");
        alarmIntent.putExtra(AirAzureDownloadService.LATITUDE_LIMIT_TOP, "0");
        alarmIntent.putExtra(AirAzureDownloadService.LATITUDE_LIMIT_BOTTOM, "0");
        alarmIntent.setAction(AirAzureDownloadService.DOWNLOAD_AZURE_AIR_DATA);
        //Wrap in a pending intent which only fires once.
        Log.v("OnCreate","Alarm Intent Created");
        PendingIntent pi = PendingIntent.getBroadcast(SmogMap.this, 0,alarmIntent,PendingIntent.FLAG_ONE_SHOT);//getBroadcast(context, 0, i, 0);

        AlarmManager am=(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        Log.v("OnCreate", "AlarmManager Created");
        //Set the AlarmManager to wake up the system.
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10, pi);


//          gps=new GPSTracker(getApplicationContext());
            SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        //getLoaderManager().initLoader(AZURE_LOADER, null, this);

    }

    @Override
    public void onMapReady(GoogleMap map) {

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        map.setContentDescription("Google Map with polygons.");


        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(33.685570, 73.023332), 17));
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        SimpleCursorLoader simpleCursorLoader=new SimpleCursorLoader(getApplicationContext(),map) {
            @Override
            public Cursor loadInBackground() {
                AirAzureDbHelper airAzureDbHelper=new AirAzureDbHelper(getApplicationContext());
                SQLiteDatabase db=airAzureDbHelper.getReadableDatabase();
                String[] projection=new String[]{
                        AirAzureContract.AirAzureEntry.COLUMN_AIR_INDEX,
                        AirAzureContract.AirAzureEntry.COLUMN_LAT,
                        AirAzureContract.AirAzureEntry.COLUMN_LONG
                };
                Cursor mCursor;
                mCursor=db.query(AirAzureContract.AirAzureEntry.TABLE_NAME,projection,null,null,null,null,null);
                return mCursor;
            }
        };
        Cursor cursor;
        cursor=simpleCursorLoader.loadInBackground();
        // Create the gradient.
        int[] colors = {
                Color.rgb(0, 255, 0), // green
                Color.rgb(255,255,0),
                Color.rgb(220, 0, 0)    // red
        };

        float[] startPoints = {
                0f, 0.5f,1f
        };

        Gradient gradient = new Gradient(colors, startPoints);

        List<WeightedLatLng> list = new ArrayList<WeightedLatLng>();
        WeightedLatLng[][] weightedLatLng=new WeightedLatLng[50][50];
        int k=0;
        Log.v("CusorLength:"," cursor length is "+cursor.getCount());
            for (int i = 0; i < weightedLatLng.length; i++) {
                for (int j = 0; j < weightedLatLng[0].length; j++) {
                    if(cursor.moveToFirst()) {
                        cursor.moveToPosition(k);
                        double random = Double.valueOf(cursor.getString(0));
                        weightedLatLng[i][j] = new WeightedLatLng(new LatLng(Double.valueOf(cursor.getString(1)), Double.valueOf(cursor.getString(2))), random);
                        Log.v("RandomValue", "random smog value: " + random);
                        list.add(weightedLatLng[i][j]);
                        k++;
                    }
                }
            }

        if(cursor.getCount()>0) {
            //Make a Weighted heatmap of the Smog
            HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                    .weightedData(list)
                    .gradient(gradient)
                    .opacity(0.3)
                    .radius(10)
                    .build();
            map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        }
        googleMap=map;
        // Add a tile overlay to the map, using the heat map tile provider.

        //CreateMapOverlay createMapOverlay=new CreateMapOverlay();
        //createMapOverlay.execute(map);

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


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v("Loader","Enter the OnCreateLoader method");

        String[] projection=new String[]{
                AirAzureContract.AirAzureEntry.COLUMN_AIR_INDEX,
                AirAzureContract.AirAzureEntry.COLUMN_LAT,
                AirAzureContract.AirAzureEntry.COLUMN_LONG
        };

        String sLatLongLimit =
                AirAzureContract.AirAzureEntry.TABLE_NAME+
                        "." + AirAzureContract.AirAzureEntry.COLUMN_LONG + " >= ? AND "
                            + AirAzureContract.AirAzureEntry.COLUMN_LONG + " <= ? AND "
                            + AirAzureContract.AirAzureEntry.COLUMN_LAT  + " <= ? AND "
                            + AirAzureContract.AirAzureEntry.COLUMN_LAT + " >= ?";

        AirAzureDbHelper mOpenHelper=new AirAzureDbHelper(SmogMap.this);

        String[] selectionArgs=new String[]{
                LONG_LEFT_LIM,
                LONG_RIGHT_LIM,
                LAT_TOP_LIM,
                LAT_BOTTOM_LIM
        };

        String sortOrder = AirAzureContract.AirAzureEntry.COLUMN_LAT + " ASC";
        SQLiteDatabase db=mOpenHelper.getReadableDatabase();
        Cursor  data= db.query(AirAzureContract.AirAzureEntry.TABLE_NAME,
                projection, null, null, null, null, sortOrder);
        Log.v("Loader", "Entered the OnLoadFinished Method");

        Log.v("Loader","about to return data form cache data table");
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}