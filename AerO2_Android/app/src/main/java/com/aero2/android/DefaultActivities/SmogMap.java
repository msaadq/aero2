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
import android.support.v7.app.AppCompatActivity;

import com.aero2.android.DefaultClasses.GPSTracker;
import com.aero2.android.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.Arrays;
import java.util.List;

/**
 * This shows how to draw polygons on a map.
 */
public class SmogMap extends AppCompatActivity implements OnMapReadyCallback {

    private static LatLng CURRENT_LAT_LONG;

    private static GPSTracker gps;

    private static final int MAXIMUN_SMOG_VALUE=1024;

    private static final double HALF_SQUARE_WIDTH=0.0001;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smog_map);

        gps=new GPSTracker(getApplicationContext());
        CURRENT_LAT_LONG=new LatLng(Double.valueOf(gps.getGps()[1]),Double.valueOf(gps.getGps()[0]));

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        map.setContentDescription("Google Map with polygons.");
        double smogValue=1024;

        //adding a square polygone to the map
        //the color of the polygon varies with smog values
        map.addPolygon(getPolygonOptions(CURRENT_LAT_LONG, smogValue));


        // Move the map so that it is centered on the mutable polygon.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(CURRENT_LAT_LONG, 18));
        map.getUiSettings().setZoomControlsEnabled(true);

        //add a marker at the place the user is standing.
        //when touched it shows the smog value at the current position.
        MarkerOptions markerOptions=new MarkerOptions().draggable(false)
                .flat(true)
                .position(CURRENT_LAT_LONG)
                .title("Smog: 225")
                .alpha(5);
        map.addMarker(markerOptions);
    }

    /**
     * Creates a List of LatLngs that form a 1km square with the latitude and longitude at its center.
     */
    private List<LatLng> createRectangle(LatLng center) {
        return Arrays.asList(new LatLng(center.latitude - HALF_SQUARE_WIDTH, center.longitude - HALF_SQUARE_WIDTH),
                new LatLng(center.latitude - HALF_SQUARE_WIDTH, center.longitude + HALF_SQUARE_WIDTH),
                new LatLng(center.latitude + HALF_SQUARE_WIDTH, center.longitude + HALF_SQUARE_WIDTH),
                new LatLng(center.latitude + HALF_SQUARE_WIDTH, center.longitude - HALF_SQUARE_WIDTH),
                new LatLng(center.latitude - HALF_SQUARE_WIDTH, center.longitude - HALF_SQUARE_WIDTH));
    }

    private PolygonOptions getPolygonOptions(LatLng center,double smogValue){

        //making the fill color using
        //120 as the transparency
        //and using the smog value obtained to set the color
        int fillColor = Color.HSVToColor(
                120, new float[]{Float.valueOf(String.valueOf(MAXIMUN_SMOG_VALUE-smogValue)), 1, 1});


        // Create a 1km square centered at current location
        //Setting the fillColor of the Square according to the value of the smog
        //The square has no outline because the stokeWidth is set to zero
        PolygonOptions options = new PolygonOptions()
                .addAll(createRectangle(center))
                .fillColor(fillColor)
                .strokeWidth(0);
        return options;
    }

}