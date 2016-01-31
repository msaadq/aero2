package com.aero2.android.DefaultActivities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.aero2.android.DefaultActivities.Data.AirAzureContract;
import com.aero2.android.DefaultActivities.Data.AirAzureDbHelper;
import com.aero2.android.DefaultClasses.AerOUtilities;
import com.aero2.android.R;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.ArrayList;

public class RecordingCompleteActivity extends AppCompatActivity {

    public final String LOG_TAG="AerO2 Recording Complete";
    private MapView mapView = null;
    public ImageView waterScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_complete);
        waterScreen=(ImageView) findViewById(R.id.waterScreen);
        mapView = (MapView) findViewById(R.id.mapboxMapView);
        startUpScreen();
        mapView.setStyleUrl("mapbox://styles/muddassir235/cik2moulv019bbpm3hlr90t6c");

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

        addSamplesToMap(cursor);
        mapView.onCreate(savedInstanceState);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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


    public void addSamplesToMap(Cursor cursor) {
        Log.v(LOG_TAG, "Entered the add samples to map");
        Log.v("CusorLength:", " cursor length is " + cursor.getCount());
        if (cursor.moveToFirst()) {

            for (int i = 0; i < cursor.getCount(); i++) {

                cursor.moveToPosition(i);
                Double latitude1=Double.valueOf(cursor.getString(1));
                Double longitude1=Double.valueOf(cursor.getString(2));
                Double airIndex1=Double.valueOf(cursor.getString(0));
                Double latitude2;
                Double longitude2;
                Double airIndex2;
                Double averageAirIndex;
                if(cursor.moveToPosition(i+1)){
                    latitude2=Double.valueOf(cursor.getString(1));
                    longitude2=Double.valueOf(cursor.getString(2));
                    airIndex2=Double.valueOf(cursor.getString(0));
                    averageAirIndex=airIndex1+airIndex2;

                    ArrayList<LatLng> endPoints=new ArrayList<LatLng>();
                    endPoints.add(new LatLng(latitude1,longitude1));
                    endPoints.add(new LatLng(latitude2,longitude2));
                    mapView.addPolyline(new PolylineOptions()
                                    .addAll(endPoints)
                                    .alpha(0.7f)
                                    .color(AerOUtilities.getColorFromSmogValue(averageAirIndex, 1000))
                                    .width(20f)
                    );
                }
            }
        }
    }

    public void startUpScreen(){
        waterScreen.setVisibility(View.VISIBLE);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fadeOutWaterScreen();
            }
        }, 1000);
    }

    public void fadeOutWaterScreen(){
        AlphaAnimation animation = new AlphaAnimation(1.0f, 0f);
        animation.setFillAfter(true);
        animation.setDuration(1000);
        waterScreen.startAnimation(animation);
    }
}
