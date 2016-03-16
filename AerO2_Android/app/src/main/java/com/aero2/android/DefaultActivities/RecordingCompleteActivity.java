package com.aero2.android.DefaultActivities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aero2.android.DefaultActivities.Data.AirAzureContract;
import com.aero2.android.DefaultActivities.Data.AirAzureDbHelper;
import com.aero2.android.DefaultClasses.AerOUtilities;
import com.aero2.android.DefaultClasses.MagicTextView;
import com.aero2.android.DefaultClasses.SystemBarTintManager;
import com.aero2.android.R;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngZoom;
import com.mapbox.mapboxsdk.views.MapView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RecordingCompleteActivity extends AppCompatActivity {

    public final String LOG_TAG="AerO2 Recording Complete";
    private MapView mapView = null;
    public ImageView waterScreen;
    public CardView summaryCard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_complete);
        waterScreen=(ImageView) findViewById(R.id.waterScreen);
        summaryCard=(CardView) findViewById(R.id.summaryCard);
        mapView = (MapView) findViewById(R.id.mapboxMapView);
        startUpScreen();
        mapView.setStyleUrl("mapbox://styles/muddassir235/cik2moulv019bbpm3hlr90t6c");

        Double displayHieght= Double.valueOf(AerOUtilities.getDisplayHieght(getApplicationContext()));
        Double cardHieghtDouble=(displayHieght*9)/25;
        int cardHieght=cardHieghtDouble.intValue();
        Log.v(LOG_TAG,"Card Height: "+cardHieght);
        RelativeLayout.LayoutParams paramsSummary = (RelativeLayout.LayoutParams) summaryCard.getLayoutParams();
        paramsSummary.height=cardHieght;
        summaryCard.setLayoutParams(paramsSummary);

        setupStatusBar();
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
        Double sumofAirIndexes=0.0;
        Double maxLat=-90.0;
        Double minLat=90.0;
        Double maxLong=-180.0;
        Double minLong=180.0;
        Log.v(LOG_TAG, "Entered the add samples to map");
        Log.v("CusorLength:", " cursor length is " + cursor.getCount());
        if (cursor.moveToFirst()) {

            for (int i = 0; i < cursor.getCount(); i++) {

                cursor.moveToPosition(i);
                Double latitude1=Double.valueOf(cursor.getString(1));
                Double longitude1=Double.valueOf(cursor.getString(2));
                Double airIndex1=Double.valueOf(cursor.getString(0));
                if(latitude1<minLat){
                    minLat=latitude1;
                }
                if(latitude1>maxLat){
                    maxLat=latitude1;
                }
                if(longitude1<minLong){
                    minLong=longitude1;
                }
                if(longitude1>maxLong){
                    maxLong=longitude1;
                }
                Double latitude2;
                Double longitude2;
                Double airIndex2;
                Double averageAirIndex;
                if(cursor.moveToPosition(i+1)){
                    latitude2=Double.valueOf(cursor.getString(1));
                    longitude2=Double.valueOf(cursor.getString(2));
                    airIndex2=Double.valueOf(cursor.getString(0));
                    averageAirIndex=(airIndex1+airIndex2)/2;

                    ArrayList<LatLng> endPoints=new ArrayList<LatLng>();
                    endPoints.add(new LatLng(latitude1,longitude1));
                    endPoints.add(new LatLng(latitude2,longitude2));
                    mapView.addPolyline(new PolylineOptions()
                                    .addAll(endPoints)
                                    .alpha(0.7f)
                                    .color(AerOUtilities.getColorFromSmogValue(averageAirIndex, 1000))
                                    .width(40f)
                    );
                }
                sumofAirIndexes+=airIndex1;
            }
            Double meanAirindex=sumofAirIndexes/cursor.getCount();

            MagicTextView averageAirIndexView=(MagicTextView) findViewById(R.id.average_air_index_tv);
            averageAirIndexView.setText(String.valueOf(meanAirindex.intValue()));
            averageAirIndexView.setTextColor(AerOUtilities.getColorFromSmogValue(meanAirindex, 1000));
            averageAirIndexView.setAlpha(0.6f);

            TextView numberOfPointTV=(TextView) findViewById(R.id.number_of_points_recorded);
            numberOfPointTV.setText(String.valueOf(cursor.getCount()));

            Long timeConsumed=getIntent().getLongExtra("TimeConsumed",0);
            TextView timeTaken=(TextView) findViewById(R.id.number_time_taken);
            timeTaken.setText(getReadableTimeInterval(timeConsumed));

            CharSequence nameOfPlace=getIntent().getCharSequenceExtra("NameOfPlace");
            TextView nameOfPlaceRecordingTookPlace=(TextView) findViewById(R.id.name_of_place);
            nameOfPlaceRecordingTookPlace.setText(nameOfPlace);
            final double longInterval = 0.000215901261691 ;
            final double latInterval = 0.000179807875453;
            double latLngRatio=latInterval/longInterval;
            Double maxLatInterval=Math.abs(maxLat-minLat);
            Double maxLongInterval=Math.abs(maxLong-minLong);
            Double longEquivalentOfmaxLatInterval=maxLatInterval/latLngRatio;
            Double maxInterval;
            if(maxLongInterval>=longEquivalentOfmaxLatInterval){
                maxInterval=maxLongInterval;
            }else {
                maxInterval=longEquivalentOfmaxLatInterval;
            }
            Log.v(LOG_TAG," max coordinate interval: "+maxInterval);
            Double midLat=(minLat+maxLat)/2;
            Double midLong=(minLong+maxLong)/2;
            Log.v(LOG_TAG,"zoom Level: "+getZoomFromInterval(maxInterval)+ " lat: "+midLat+" long: "+midLong);
            mapView.setLatLng(new LatLngZoom(midLat,midLong,getZoomFromInterval(maxInterval)));
            mapView.setAllGesturesEnabled(false);
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

    public void setupStatusBar(){
        CoordinatorLayout coordinatorLayout=(CoordinatorLayout) findViewById(R.id.summaryLayout);
        // Set the padding to match the Status Bar height
        coordinatorLayout.setPadding(0, -AerOUtilities.getStatusBarHeight(this), 0, 0);
        // create our manager instance after the content view is set
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setStatusBarAlpha(0.2f);
        tintManager.setNavigationBarAlpha(0.2f);
        tintManager.setTintAlpha(0.2f);
        tintManager.setStatusBarTintResource(R.drawable.selected);
        tintManager.setTintColor(Color.parseColor("#39ADCC"));

    }

    public Double getZoomFromInterval(Double maxInterval){
        Double intervalAtMinZoom=270.0;
        Double minInterval=0.0009763894426;
        Double exponand=Math.exp(Math.log(intervalAtMinZoom/minInterval)/22);
        Double zoomLevel=(Math.log((maxInterval*2)/minInterval)/Math.log(exponand));
        return 22-Math.abs(zoomLevel);
    }

    public String getReadableTimeInterval(Long interval){
        long totalSeconds=interval/1000;
        long seconds=totalSeconds%60;
        long totalMins=totalSeconds/60;
        long mins=totalMins%60;
        long totalHours=totalMins/60;
        long hours=totalHours%24;
        long days=hours/24;
        String readableTimeInterval;
        if(days!=0) {
            readableTimeInterval =days+" days "+hours+" hours "+mins+" mins "+seconds+" sec ";
        }else if(hours!=0){
            readableTimeInterval =hours+" hours "+mins+" mins "+seconds+" sec ";
        }else if(mins!=0){
            readableTimeInterval =mins+" mins "+seconds+" sec ";
        }else if(seconds!=0){
            readableTimeInterval =seconds+" seconds ";
        }else{
            readableTimeInterval="0 seconds";
        }
        return readableTimeInterval;
    }
}
