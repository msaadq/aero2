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
import android.app.Dialog;
import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.google.android.gms.maps.CameraUpdate;
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
import java.util.Calendar;
import java.util.List;


public class SmogMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private final String LOG_TAG = "SmogMapActivity";

    //Splash Screen
    protected Dialog splashScreen;

    //Variables related to Smog Map
    public GoogleMap googleMap;
    public Double mapLat;
    public Double mapLong;
    public float mapZoom;

    //Variables related to Location
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private static final int MY_PERMISSIONS_REQUEST = 2;
    Double currLat, currLong;

    //UI ELEMENTS
    private FloatingActionButton currentLocationButton;
    private FloatingActionButton recordActivityButtom;

    //Status check booleans
    private boolean ALARM_NOT_CALLED;
    public  boolean SERVICE_COMPLRETED;
    private boolean permissionJustGranted;





    //START// $$$$$$$$$$$$$$$ APP LIFECYCLE $$$$$$$$$$$$$$$$$$$$$$$
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //show splash screen while map loads
        showSplashScreen();

        setContentView(R.layout.smog_map);
        showMapHideLoadingScreen();

        Log.v("Smog Map", "Entered the smog map activity");
        recordActivityButtom = (FloatingActionButton) findViewById(R.id.record);
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

    @Override
    protected void onStart() {
        Log.v("onStart", "Entered the onStart Function of the Smog Map Activity");
        mGoogleApiClient.connect();
        ALARM_NOT_CALLED = getAlarmStatus();
        Log.v(LOG_TAG, "Alarm status retrieved " + ALARM_NOT_CALLED);
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveCurrentAppState();

    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    //refresh current activity
    public void refreshActivity(){
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }
    //END// $$$$$$$$$$$$$$$ APP LIFECYCLE $$$$$$$$$$$$$$$$$$$$$$$





    //START// *******************  ALL MAP RELATED FUCTIONS  ********************************
    @Override
    public void onMapReady(GoogleMap map) {

        //smoothly fade away splash screen
        fadeSplashScreen();

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        map.setContentDescription("Google Map with polygons.");
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        map.getUiSettings().setTiltGesturesEnabled(false);
        googleMap = map;
        //Moving camera to last known location
        restorePreviousMapState();


        //if the user hasn't granted location permissions to the app
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                removeSplashScreen();
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                removeSplashScreen();
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
        map.getUiSettings().setMyLocationButtonEnabled(false);
        currentLocationButton = (FloatingActionButton) findViewById(R.id.myLocation);
        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add code for showing legend dialog here
                moveCameraToMyLocation();
            }
        });
        //add heat map to the instance of google map
        addHeatMap(map);


    }

    //restore previous map state i.e. last time the app was closed
    public void restorePreviousMapState(){
        //Retrieving the last known location from cache
        SharedPreferences latSharedPref = getApplicationContext().getSharedPreferences("MapLatitudeAerO2", Context.MODE_PRIVATE);
        SharedPreferences longSharedPref = getApplicationContext().getSharedPreferences("MapLongitudeAerO2", Context.MODE_PRIVATE);
        SharedPreferences zoomPref = getApplicationContext().getSharedPreferences("MapZoomAerO2", Context.MODE_PRIVATE);
        currLat = Double.valueOf(latSharedPref.getString("MapLatitudeAerO2", "33"));
        currLong = Double.valueOf(longSharedPref.getString("MapLongitudeAerO2", "72"));
        float zoom=Float.valueOf(zoomPref.getString("MapZoomAerO2","18"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currLat, currLong), zoom));
        mapLat=googleMap.getCameraPosition().target.latitude;
        mapLong=googleMap.getCameraPosition().target.longitude;
        mapZoom=googleMap.getCameraPosition().zoom;

    }

    //Add heat map of smog data
    public boolean addHeatMap(GoogleMap map){
        Log.v(LOG_TAG, "Entered the addHeatMapFunction");
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
        } else {
            db.close();
            cursor.close();
            return false;
        }
    }

    //Gets WeightedLatLong list (weighted with air index values) for the heatmap
    public List<WeightedLatLng> getListForHeatMap(Cursor cursor) {
        Log.v(LOG_TAG, "Entered the getListFotHeatMap Fuction");
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

    //Setup Alarm for "data download service"
    public void setupAlarm(int hourOfDay){
        //Alarm has been called
        ALARM_NOT_CALLED = false;

        //Setup what the alarm has to do when it goes off.
        Intent alarmIntent = new Intent(SmogMapActivity.this, AirAzureDownloadService.AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(SmogMapActivity.this, 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);//getBroadcast(context, 0, i, 0);
        Log.v("OnCreate", "Alarm Intent Created");

        //Set the AlarmManager to wake up the system every day at 6 a.m.
        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        long timeEveryDay=calendar.getTimeInMillis();
        long currentTime=System.currentTimeMillis();
        long oneMin=60*1000;
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, timeEveryDay, AlarmManager.INTERVAL_DAY, pi);
        if((currentTime+oneMin)<timeEveryDay){
            Log.v(LOG_TAG,"First time download and time is before 6 a.m.");
            Intent firstTimeDownloadIntent = new Intent(getApplicationContext(), AirAzureDownloadService.class);
            startService(firstTimeDownloadIntent);
        }
    }

    //notify system (i.e. save status in cache) and notify user that the download service has completed
    public void notifyServiceComplete(){
        //Set the boolean in cache to state true i.e. the service is complete.
        SharedPreferences afterServiceCompletion=getApplicationContext().getSharedPreferences("AFTER_SERVICE_COMPLETED",Context.MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor afterServiceCompletionEditor = afterServiceCompletion.edit();
        afterServiceCompletionEditor.putBoolean("AFTER_SERVICE_COMPLETED", true);
        afterServiceCompletionEditor.commit();

        //Notify the User that the download service has been completed
        int mNotificationId = 235;
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Data AerO2")
                        .setContentText("Smog Data Download Complete");
        NotificationManager mNotifyMgr =
                (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent(this, SmogMapActivity.class);

        //What to do if the notification is clicked??
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

        /*
         Once the service is complete notify the system that service isn't
         completed so that it doesn't refresh the activity over and over again.
         sorry this might seen a bit wiered but this is how it works.
        */
        SharedPreferences serviceStatus=getApplicationContext().getSharedPreferences("SERVICE_COMPLETED",Context.MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor serviceStatusEdit=serviceStatus.edit();
        serviceStatusEdit.putBoolean("SERVICE_COMPLETED", false);
        serviceStatusEdit.commit();


    }

    //move map camera to the uers's current positon
    private void moveCameraToMyLocation() {
        LatLng latLng = new LatLng(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        googleMap.animateCamera(cameraUpdate);
    }
    //END// *******************  ALL MAP RELATED FUCTIONS  ********************************





    //START// *******************  ALL UI RELATED FUCTIONS  ********************************
    protected void showSplashScreen() {
        SharedPreferences afterServiceCompletion=getApplicationContext().getSharedPreferences("AFTER_SERVICE_COMPLETED", Context.MODE_WORLD_WRITEABLE);
        SharedPreferences afterPermissionGranted = getApplicationContext().getSharedPreferences("AFTER_PERMISSION_GRANTED", Context.MODE_WORLD_WRITEABLE);
        boolean serviceJustCompleted=afterServiceCompletion.getBoolean("AFTER_SERVICE_COMPLETED", false);
        splashScreen = new Dialog(this, R.style.SplashScreen);
        permissionJustGranted=afterPermissionGranted.getBoolean("AFTER_PERMISSION_GRANTED",false);

        if(!serviceJustCompleted&&!permissionJustGranted) {

//            View splashIcon=(View) findViewById(R.id.splash_icon);
//            View splashText=(View) findViewById(R.id.splash_text);
//            splashIcon.setVisibility(View.GONE);
//            splashText.setVisibility(View.GONE);
            splashScreen.setContentView(R.layout.splash_screen);
            splashScreen.setCancelable(false);
            splashScreen.show();
        }else if(serviceJustCompleted) {
            SharedPreferences.Editor afterServiceCompletionEditor=afterServiceCompletion.edit();
            afterServiceCompletionEditor.putBoolean("AFTER_SERVICE_COMPLETED", false);
            afterServiceCompletionEditor.commit();
            splashScreen.setCancelable(false);
            splashScreen.show();
        }else if(permissionJustGranted){
            removeSplashScreen();
        }




    }

    protected void removeSplashScreen() {
        if (splashScreen!= null) {
            splashScreen.dismiss();
            splashScreen = null;
        }
    }

    public void fadeSplashScreen() {
        // Set Runnable to remove splash screen just in case
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                removeSplashScreen();
            }
        }, 1000);

        if(!permissionJustGranted) {
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.map_layout);
            AlphaAnimation animation = new AlphaAnimation(-2f, 1.0f);
            animation.setFillAfter(true);
            animation.setDuration(1500);

            //apply the animation ( fade In ) to your LAyout
            layout.startAnimation(animation);
        }

    }

    public void showMapHideLoadingScreen(){
        ProgressBar progressBar=(ProgressBar) findViewById(R.id.progressBar);
        View loadingTV=(View) findViewById(R.id.loading_tv);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FF00BEED"), PorterDuff.Mode.MULTIPLY);
        progressBar.setVisibility(View.GONE);
        loadingTV.setVisibility(View.GONE);
    }

    public void showNoInternetScreen() {
        showLoadingScreen();
        ProgressBar progressBar=(ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        TextView noConnectionTV=(TextView) findViewById(R.id.loading_tv);
        noConnectionTV.setText("NO INTERNET CAN'T DOWNLOAD DATA FOR THE FIRST TIME :(");
        noConnectionTV.setTextColor(Color.GRAY);
        noConnectionTV.setPadding(40,30,40,0);
    }

    public void hideNoInternetScreen() {
        hideLoadingScreen();
        TextView ConnectionTV = (TextView) findViewById(R.id.loading_tv);
        ConnectionTV.setText("Loading smog data");
        ConnectionTV.setTextColor(Color.parseColor("#FF00BEED"));
        ConnectionTV.setPadding(0,30,0,30);
    }

    public void showLoadingScreen(){
        View mapFragment=(View) findViewById(R.id.map);
        mapFragment.setVisibility(View.GONE);
        View button1=(View) findViewById(R.id.myLocation);
        View button2=(View) findViewById(R.id.record);
        View progressBar=(View) findViewById(R.id.progressBar);
        View loadingTV=(View) findViewById(R.id.loading_tv);
        button1.setVisibility(View.GONE);
        button2.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        loadingTV.setVisibility(View.VISIBLE);
    }

    public void hideLoadingScreen(){
        View progressBar=(View) findViewById(R.id.progressBar);
        View loadingTV=(View) findViewById(R.id.loading_tv);
        progressBar.setVisibility(View.GONE);
        loadingTV.setVisibility(View.GONE);
    }

    public void animateExit(){
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.map_layout);
        AlphaAnimation animation = new AlphaAnimation(1.0f , 0.0f ) ;
        animation.setFillAfter(true);
        animation.setDuration(1000);
        //apply the animation ( fade In ) to your LAyout
        layout.startAnimation(animation);

    }
    //END// *******************  ALL UI RELATED FUCTIONS  ********************************





    //START// *******************  ALL LOCATION RELATED FUCTIONS  ********************************
    @Override
    public void onConnected(Bundle bundle) {
        Log.v(LOG_TAG, "Entered the onConnected method");
        //request the current location
        requestLocation();
    }

    //request current location from the google play locations api
    public void requestLocation(){
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(7000);
        mLocationRequest.setFastestInterval(7000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //if the user hasn't granted the app location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                removeSplashScreen();
            } else {
                // No explanation needed, we can request the permission.
                removeSplashScreen();
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

        saveMapStateAndLocationInRAM(location);
        /*
           storing the latest accurate know location in cache so that if location
           services are uavailable the last known location can be used.
        */

        Log.v(LOG_TAG, " " + currLat + " " + currLong);
        if (ALARM_NOT_CALLED) {
            if(networkIsAvailable()) {

                hideNoInternetScreen();
                //if the Alarm hasn't been called even once since the install of the app

                //showing loading screen as data is about to be downloaded
                showLoadingScreen();

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currLat, currLong), 16));

                //saving current location in cache so that the alarm service can load data for the current location
                saveCurrentLocationInCache();

                //setting up alarm to call download service every day at 6 a.m.
                setupAlarm(6);

            }else{
                showNoInternetScreen();
            }
        }

        // has the service been completed ??
        if(serviceHasCompleted()){

            Toast.makeText(getApplicationContext(), "Smog data downloaded",Toast.LENGTH_SHORT).show();

            notifyServiceComplete();
            hideLoadingScreen();
            animateExit();
            refreshActivity();
        }
    }

    @Override //This function is called once the dialog for asking permission is answered
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

                    locationPermissionHasBeenGranted();
                    //once the permission to access fine location is granted restart the activity so that we can get the user's location
                    refreshActivity();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }

    }
    //END// *******************  ALL LOCATION RELATED FUCTIONS  ********************************





    //START// *******************  ALL STATUS GETTERS (i.e. such is network available e.t.c.)  ********************************
    //whether the network is available or not?
    private boolean networkIsAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //has the "download service" alarm been set or not
    public boolean getAlarmStatus(){
        SharedPreferences alarmPref = getApplicationContext().getSharedPreferences("ALARM_NOT_CALLED", Context.MODE_PRIVATE);
        return alarmPref.getBoolean("ALARM_NOT_CALLED", true);
    }

    //whether the download service has completed or not
    public boolean serviceHasCompleted(){
        //retrieving the value of service completion status
        SharedPreferences serviceStatus=getApplicationContext().getSharedPreferences("SERVICE_COMPLETED",Context.MODE_WORLD_WRITEABLE);
        return serviceStatus.getBoolean("SERVICE_COMPLETED",false);
    }
    //END// *******************  ALL STATUS GETTERS (i.e. such is network available e.t.c.)  ********************************





    //START// *******************  SAVE STUFF  ********************************
    //Save current app state such as state of map, current known location, e.t.c. in cache
    public void saveCurrentAppState(){
        //Saving Current Map State
        SharedPreferences latMapSharedPref = getApplicationContext().getSharedPreferences("MapLatitudeAerO2", Context.MODE_PRIVATE);
        SharedPreferences longMapSharedPref = getApplicationContext().getSharedPreferences("MapLongitudeAerO2", Context.MODE_PRIVATE);
        SharedPreferences zoomPref = getApplicationContext().getSharedPreferences("MapZoomAerO2", Context.MODE_PRIVATE);
        SharedPreferences.Editor latMapEdit = latMapSharedPref.edit();
        SharedPreferences.Editor longMapEdit = longMapSharedPref.edit();
        SharedPreferences.Editor zoomPrefEdit=zoomPref.edit();
        if(mapLat!=null) {
            latMapEdit.putString("MapLatitudeAerO2", String.valueOf(mapLat));
            latMapEdit.commit();
            longMapEdit.putString("MapLongitudeAerO2", String.valueOf(mapLong));
            longMapEdit.commit();
            zoomPrefEdit.putString("MapZoomAerO2", String.valueOf(mapZoom));
            zoomPrefEdit.commit();
        }else{
            Log.v(LOG_TAG,"Latitude was null");
        }
        //Saving Current Location data
        SharedPreferences latSharedPref = getApplicationContext().getSharedPreferences("LatitudeAerO2", Context.MODE_PRIVATE);
        SharedPreferences longSharedPref = getApplicationContext().getSharedPreferences("LongitudeAerO2", Context.MODE_PRIVATE);
        SharedPreferences.Editor latEdit = latSharedPref.edit();
        if(currLat!=null) {
            latEdit.putString("LatitudeAerO2", String.valueOf(currLat));
            latEdit.commit();
            SharedPreferences.Editor longEdit = longSharedPref.edit();
            longEdit.putString("LongitudeAerO2", String.valueOf(currLong));
            longEdit.commit();
        }else{
            Log.v(LOG_TAG,"Latitude was null");
        }
        Log.v(LOG_TAG,"Save Current map state");
        if(permissionJustGranted) {
            SharedPreferences afterPermissionGranted=getApplicationContext().getSharedPreferences("AFTER_PERMISSION_GRANTED", Context.MODE_WORLD_WRITEABLE);
            SharedPreferences.Editor afterPermissionGrantedEditor = afterPermissionGranted.edit();
            afterPermissionGrantedEditor.putBoolean("AFTER_PERMISSION_GRANTED", false);
            afterPermissionGrantedEditor.commit();
        }
    }

    //Save current map and location state in ram so that it can be accesed from anywhere in the activity
    public void saveMapStateAndLocationInRAM(Location location){
        currLat = location.getLatitude();
        currLong = location.getLongitude();
        mapLat=googleMap.getCameraPosition().target.latitude;
        mapLong=googleMap.getCameraPosition().target.longitude;
        mapZoom=googleMap.getCameraPosition().zoom;
    }

    //save current loaction in cache so that it can be accesed from anywhere.
    public void saveCurrentLocationInCache(){
        SharedPreferences latSharedPref = getApplicationContext().getSharedPreferences("LatitudeAerO2", Context.MODE_PRIVATE);
        SharedPreferences longSharedPref = getApplicationContext().getSharedPreferences("LongitudeAerO2", Context.MODE_PRIVATE);
        SharedPreferences.Editor latEdit = latSharedPref.edit();
        latEdit.putString("LatitudeAerO2", String.valueOf(currLat));
        latEdit.commit();
        SharedPreferences.Editor longEdit = longSharedPref.edit();
        longEdit.putString("LongitudeAerO2", String.valueOf(currLong));
        longEdit.commit();
    }

    //save in cache that permission to access device's location has been granted
    public void locationPermissionHasBeenGranted(){
        SharedPreferences afterServiceCompletion=getApplicationContext().getSharedPreferences("AFTER_PERMISSION_GRANTED",Context.MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor afterServiceCompletionEditor=afterServiceCompletion.edit();
        afterServiceCompletionEditor.putBoolean("AFTER_PERMISSION_GRANTED", true);
        afterServiceCompletionEditor.commit();
    }
    //END// *******************  SAVE STUFF  ********************************
}