package com.aero2.android;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import com.aero2.android.DefaultActivities.Data.AirAzureContract;
import com.aero2.android.DefaultActivities.Data.AirAzureDbHelper;
import com.aero2.android.DefaultActivities.SimpleCursorLoader;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.layers.CustomLayer;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.ArrayList;

public class MapBoxActivity extends AppCompatActivity {

    public static final String LOG_TAG="AerO2 Map box Activity";


    private static final int MY_PERMISSIONS_REQUEST = 2;

    //location variables
    public Double currLat;
    public Double currLong;

    //map varaibles
    private MapView mapView = null;
    private FloatingActionButton goToCurretntLocation;
    private FloatingActionButton startRecordingSmog;
    public Double mapLat;
    public Double mapLong;
    public Double mapZoom;
    public Double maxAirIndex= 1024.0;
    public int alpha=50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_box);
        bindViews();
        setOnClickListeners();

        mapView = (MapView) findViewById(R.id.mapboxMapView);
        mapView.setStyleUrl("mapbox://styles/muddassir235/cijqzvhxo00568zkqbk87dftn");
        checkForLocationPermission();
        mapView.setMyLocationEnabled(true);
        mapView.onCreate(savedInstanceState);
        restorePreviousMapState();
    }


    public void bindViews(){
        goToCurretntLocation=(FloatingActionButton) findViewById(R.id.myLocation);
        startRecordingSmog=(FloatingActionButton) findViewById(R.id.record);
    }

    public void setOnClickListeners(){
        goToCurretntLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveCameraToMyLocation();
            }
        });
        startRecordingSmog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.refreshDrawableState();
            }
        });

    }

    //move map camera to the uers's current positon
    private void moveCameraToMyLocation() {
        LatLng latLng = new LatLng(mapView.getMyLocation().getLatitude(), mapView.getMyLocation().getLongitude());
        CameraPosition cameraPosition=new CameraPosition(latLng,14,0,0);
        CameraUpdateFactory cameraUpdateFactory=new CameraUpdateFactory();
        CameraUpdate cameraUpdate=cameraUpdateFactory.newCameraPosition(cameraPosition);
        mapView.animateCamera(cameraUpdate, 1000, null);
    }

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
            Log.v(LOG_TAG, "Latitude was null");
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
        Log.v(LOG_TAG, "Saved Current map state");
//        if(permissionJustGranted) {
//            SharedPreferences afterPermissionGranted=getApplicationContext().getSharedPreferences("AFTER_PERMISSION_GRANTED", Context.MODE_WORLD_WRITEABLE);
//            SharedPreferences.Editor afterPermissionGrantedEditor = afterPermissionGranted.edit();
//            afterPermissionGrantedEditor.putBoolean("AFTER_PERMISSION_GRANTED", false);
//            afterPermissionGrantedEditor.commit();
//        }
    }

    public void saveMapStateInRam(){
        mapLat=mapView.getLatLng().getLatitude();
        mapLong=mapView.getLatLng().getLongitude();
        mapZoom=mapView.getZoom();
    }

    public void saveMyLocationInRam(){
        currLat=mapView.getMyLocation().getLatitude();
        currLong=mapView.getMyLocation().getLongitude();
    }

    //restore previous map state i.e. last time the app was closed
    public void restorePreviousMapState(){
        //Retrieving the last known location from cache
        SharedPreferences latSharedPref = getApplicationContext().getSharedPreferences("MapLatitudeAerO2", Context.MODE_PRIVATE);
        SharedPreferences longSharedPref = getApplicationContext().getSharedPreferences("MapLongitudeAerO2", Context.MODE_PRIVATE);
        SharedPreferences zoomPref = getApplicationContext().getSharedPreferences("MapZoomAerO2", Context.MODE_PRIVATE);
        mapLat = Double.valueOf(latSharedPref.getString("MapLatitudeAerO2", "33"));
        mapLong = Double.valueOf(longSharedPref.getString("MapLongitudeAerO2", "72"));
        float zoom=Float.valueOf(zoomPref.getString("MapZoomAerO2","18"));
        LatLng latLng = new LatLng(mapLat, mapLong);
        CameraPosition cameraPosition=new CameraPosition(latLng,zoom,0,0);
        CameraUpdateFactory cameraUpdateFactory=new CameraUpdateFactory();
        CameraUpdate cameraUpdate=cameraUpdateFactory.newCameraPosition(cameraPosition);
        mapView.moveCamera(cameraUpdate);

    }


    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause()  {
        super.onPause();
        saveMapStateInRam();
        saveMyLocationInRam();
        saveCurrentAppState();
        try {
            trimCache(this);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    public void checkForLocationPermission(){
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
                    //once the permission to access fine location is granted restart the activity so that we can get the user's location

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }

    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

}
