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

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


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
public class SmogMap extends AppCompatActivity implements OnMapReadyCallback {


    //************Variables related to Smog Map***************************************

    private static LatLng CURRENT_LAT_LONG;

    private static final int MAXIMUN_SMOG_VALUE=1024;


    //************Variables related to Smog Map***************************************


    private FloatingActionButton floatingActionButton;

    private boolean gps_enabled=false;

    private boolean network_enabled=false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smog_map);
        floatingActionButton=(FloatingActionButton) findViewById(R.id.fab);


//        gps=new GPSTracker(getApplicationContext());

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        map.setContentDescription("Google Map with polygons.");


        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(33.685570, 73.023332), 17));
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

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
        WeightedLatLng[][] weightedLatLng=new WeightedLatLng[100][100];
        for(int i=0;i<weightedLatLng.length;i++){
            for(int j=0;j<weightedLatLng[0].length;j++){
                double random=-1;
                while(random<0||random>1.024){
                    random=Math.random();
                }
                if(i>70||j>70){
                    random=0.00001;
                }
                weightedLatLng[i][j]=new WeightedLatLng(new LatLng(33.685570-0.0002*j,73.023332+0.0002*i),random*1000);
                Log.v("RandomValue","random smog value: "+ random*1000);
                list.add(weightedLatLng[i][j]);
            }
        }
        //Make a Weighted heatmap of the Smog
        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .weightedData(list)
                .gradient(gradient)
                .opacity(0.3)
                .radius(10)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
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



}