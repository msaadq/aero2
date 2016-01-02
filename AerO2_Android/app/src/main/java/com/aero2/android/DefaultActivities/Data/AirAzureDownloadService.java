package com.aero2.android.DefaultActivities.Data;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aero2.android.DefaultClasses.Azure.AzureHandler;


public class AirAzureDownloadService extends IntentService {

    public static final String DOWNLOAD_AZURE_AIR_DATA =
            "com.aero2.android.DefaultActivities.Data.download.AZUREAIRDATA";
    public static final String UPDATE_LOCAL_CACHE =
            "com.aero2.android.DefaultActivities.Data.update.LOCALCACHE";
    public static final String CURRENT_LATITUDE =
            "com.aero2.android.DefaultActivities.Data.latitude.CURRENT";
    public static final String CURRENT_LONGITUDE =
            "com.aero2.android.DefaultActivities.Data.longitude.CURRENT";
    public static final String VERTICAL_INTERVAL =
            "com.aero2.android.DefaultActivities.Data.vertical.INTERVAL";
    public static final String HORIZONTAL_INTERVAL =
            "com.aero2.android.DefaultActivities.Data.horizontal.INTERVAL";

    public AirAzureDownloadService() {
        super("AirAzureDownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v("HandleIntent","Entered the OnHandleIntent of the AirAzureDownloadService");
        if (intent != null) {

                Log.v("HandleIntent","About to handle Action Download");
                final String currLat = intent.getStringExtra(CURRENT_LATITUDE);
                final String currLong = intent.getStringExtra(CURRENT_LONGITUDE);
                final String verticalInt=intent.getStringExtra(VERTICAL_INTERVAL);
                final String horizontalInt=intent.getStringExtra(HORIZONTAL_INTERVAL);
                handleActionDownloadAzureAirData(currLat, currLong, verticalInt, horizontalInt);
        }
    }

    private void handleActionDownloadAzureAirData(String currLat, String currLong,
            String verticalInt, String horizontalInt) {

        AzureHandler azureHandler;

        //Retrieves results from azure and saves in local storage
        azureHandler = new AzureHandler(this);
        azureHandler.retrieveSamples(Double.valueOf(currLat),Double.valueOf(currLong),
                Integer.parseInt(verticalInt),Integer.parseInt(horizontalInt), this);

     }

    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("AlarmReciever","Enter the AlarmReciever on Recieve Function");
            Intent sendIntent = new Intent(context, AirAzureDownloadService.class);
            sendIntent.putExtra(AirAzureDownloadService.CURRENT_LATITUDE,
                    intent.getStringExtra(AirAzureDownloadService.CURRENT_LATITUDE));
            sendIntent.putExtra(AirAzureDownloadService.CURRENT_LONGITUDE,
                    intent.getStringExtra(AirAzureDownloadService.CURRENT_LONGITUDE));
            sendIntent.putExtra(AirAzureDownloadService.VERTICAL_INTERVAL,
                    intent.getStringExtra(AirAzureDownloadService.VERTICAL_INTERVAL));
            sendIntent.putExtra(AirAzureDownloadService.HORIZONTAL_INTERVAL,
                    intent.getStringExtra(AirAzureDownloadService.HORIZONTAL_INTERVAL));

            sendIntent.setAction(intent.getAction());
            context.startService(sendIntent);
        }
    }
}
