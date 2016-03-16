package com.aero2.android.DefaultActivities.Data;

import android.Manifest;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import com.aero2.android.DefaultActivities.SmogMapActivity;
import com.aero2.android.DefaultClasses.AerOUtilities;
import com.aero2.android.DefaultClasses.DataTables.ResultDataTable;
import com.aero2.android.DefaultClasses.SQLite.ResultsSQLite;
import com.aero2.android.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import java.text.DecimalFormat;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RecordService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

    public final String LOG_TAG="AerO2 record Service";
    public static volatile boolean shouldContinue = true;

    public int sampleID;
    AirAzureDbHelper sqliteHelper;
    SQLiteDatabase sqLiteDatabase;
    ResultsSQLite resultsSQLite;

    public RecordService() {
        super("RecordService");
    }

    //Google Play services
    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    public void startRecording(Context context){
        Intent startRecordService=new Intent(context,RecordService.class);
        context.startService(startRecordService);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        if (shouldContinue == true) {
            mGoogleApiClient = new GoogleApiClient
                    .Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            //Setting up classes for storing data in Cache
            sqliteHelper = new AirAzureDbHelper(getApplicationContext());
            sqLiteDatabase = sqliteHelper.getWritableDatabase();
            resultsSQLite = new ResultsSQLite(sqLiteDatabase);

            //delete previous data if any.
            resultsSQLite.emptySQL();
            sampleID=0;

            mGoogleApiClient.connect();
        }
        if (shouldContinue == false) {
            stopSelf();
            return;
        }

    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.v(LOG_TAG, "Entered the on connected Method");
        requestLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (shouldContinue == true) {
            Double value = Math.random() * 1000;
            DecimalFormat decimalFormat = new DecimalFormat("#.###");
            String lati = decimalFormat.format(location.getLatitude());
            String longi = decimalFormat.format(location.getLongitude());
            String latitude = "<b>" + "lat: " + "</b> " + lati;
            String longitude = "<b>" + "lon: " + "</b> " + longi;

            String valueString= String.valueOf(value.intValue());
            SharedPreferences currentSmogValue = getApplicationContext().getSharedPreferences("CurrentSmogValue", Context.MODE_PRIVATE);
            SharedPreferences.Editor currentSmogValueEditor = currentSmogValue.edit();
            currentSmogValueEditor.putString("CurrentSmogValue", valueString);
            currentSmogValueEditor.commit();

            ResultDataTable sample = new ResultDataTable(String.valueOf(sampleID), 12323.0, location.getLatitude(), location.getLongitude(), value);
            resultsSQLite.addResultValue(sample);
            notifyUserOfRecord(Html.fromHtml(latitude + "&nbsp &nbsp &nbsp &nbsp;" + longitude + "&nbsp &nbsp &nbsp &nbsp;" + valueString));
            sampleID++;
        }else{

            resultsSQLite.closeDB();
            sqLiteDatabase.close();
            sqliteHelper.close();
            mGoogleApiClient.disconnect();
            stopSelf();
            return;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //request current location from the google play locations api
    public void requestLocation(){
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.v(LOG_TAG, "About to request Location");
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.v(LOG_TAG, "Requested Location");
    }

    public void notifyUserOfRecord(Spanned notification){
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Recording ...")
                        .setContentText(notification);
        Intent resultIntent = new Intent(this, SmogMapActivity.class);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        int mNotificationId = 235;
        NotificationManager mNotifyMgr =
                (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }



}
