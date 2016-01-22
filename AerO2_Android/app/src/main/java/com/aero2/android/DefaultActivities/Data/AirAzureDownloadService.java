package com.aero2.android.DefaultActivities.Data;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.aero2.android.DefaultActivities.SmogMapActivity;
import com.aero2.android.DefaultClasses.Azure.AzureHandler;
import com.aero2.android.R;


public class AirAzureDownloadService extends IntentService {

    public AirAzureDownloadService() {
        super("AirAzureDownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v("HandleIntent","Entered the OnHandleIntent of the AirAzureDownloadService");
        if (intent != null) {

                Log.v("HandleIntent","About to handle Action Download");

                //making the alarm not called false as if we have reached this scope the alarm has been called.
                SharedPreferences alarmPref = getApplicationContext().getSharedPreferences("ALARM_NOT_CALLED", Context.MODE_PRIVATE);
                SharedPreferences.Editor alarmPrefEditor = alarmPref.edit();
                alarmPrefEditor.putBoolean("ALARM_NOT_CALLED", false);
                alarmPrefEditor.commit();

                //handling download of smog data
                handleActionDownloadAzureAirData();
        }
    }

    private void handleActionDownloadAzureAirData() {

        //notifying the user of the download.
        notifyUserOfDownload();

        //Setting up classes for fetching data form azure cloud service
        AzureHandler azureHandler;

        //retrieving the latest known location from cache
        SharedPreferences latSharedPref = getApplicationContext().getSharedPreferences("LatitudeAerO2", Context.MODE_PRIVATE);
        SharedPreferences longSharedPref = getApplicationContext().getSharedPreferences("LongitudeAerO2", Context.MODE_PRIVATE);
        double currLat = Double.valueOf(latSharedPref.getString("LatitudeAerO2", "33"));
        double currLong = Double.valueOf(longSharedPref.getString("LongitudeAerO2", "72"));

        //Retrieves results from azure and saves in local storage
        azureHandler = new AzureHandler(this);
        azureHandler.retrieveSamples(currLat,currLong, 8, 8, this);

     }

    public void notifyUserOfDownload(){
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Data AerO2")
                        .setContentText("Starting Smog Data Download");
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

    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("AlarmReciever","Enter the AlarmReciever on Recieve Function");
            Intent sendIntent = new Intent(context, AirAzureDownloadService.class);
            sendIntent.setAction(intent.getAction());
            context.startService(sendIntent);
        }
    }
}
