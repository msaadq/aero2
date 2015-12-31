package com.aero2.android.DefaultActivities.Data;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AirAzureDownloadService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String DOWNLOAD_AZURE_AIR_DATA = "com.aero2.android.DefaultActivities.Data.download.AZUREAIRDATA";
    public static final String UPDATE_LOCAL_CACHE = "com.aero2.android.DefaultActivities.Data.update.LOCALCACHE";

    // TODO: Rename parameters
    public static final String LATITUDE_LIMIT_TOP = "com.aero2.android.DefaultActivities.Data.latitude.LIMITTOP";
    public static final String LATITUDE_LIMIT_BOTTOM = "com.aero2.android.DefaultActivities.Data.latitude.LIMITBOTTOM";
    public static final String LONGITUDE_LIMIT_LEFT = "com.aero2.android.DefaultActivities.Data.longitude.LIMITLEFT";
    public static final String LONGITUDE_LIMIT_RIGHT = "com.aero2.android.DefaultActivities.Data.longitude.LIMITRIGHT";

    public static Context localContext;
    static AirAzureDbHelper mDbHelper;
    public SQLiteDatabase db;

    public AirAzureDownloadService() {
        super("AirAzureDownloadService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public void startActionDownloadAzureAirData(Context context, String latLimitTop, String latLimitBottom, String longLimitLeft, String longLimitRight) {
        Log.v("StartAction","Staritng Download Action");
        localContext=context;
        mDbHelper=new AirAzureDbHelper(localContext);

        Intent intent = new Intent(context, AirAzureDownloadService.class);
        intent.setAction(DOWNLOAD_AZURE_AIR_DATA);
        intent.putExtra(LATITUDE_LIMIT_TOP, latLimitTop);
        intent.putExtra(LATITUDE_LIMIT_BOTTOM, latLimitBottom);
        intent.putExtra(LONGITUDE_LIMIT_LEFT,longLimitLeft);
        intent.putExtra(LONGITUDE_LIMIT_RIGHT,longLimitRight);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v("HandleIntent","Entered the OnHandleIntent of the AirAzureDownloadService");
        if (intent != null) {

                Log.v("HandleIntent","About to handle Action Download");
                final String latLimitTop = intent.getStringExtra(LATITUDE_LIMIT_TOP);
                final String latLimitBottom = intent.getStringExtra(LATITUDE_LIMIT_BOTTOM);
                final String longLimitLeft=intent.getStringExtra(LONGITUDE_LIMIT_LEFT);
                final String longLimitRight=intent.getStringExtra(LONGITUDE_LIMIT_RIGHT);
                handleActionDownloadAzureAirData(latLimitTop, latLimitBottom, longLimitLeft, longLimitRight);
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionDownloadAzureAirData(String latLimitTop, String latLimitBottom,String longLimitLeft,String longLimitRight) {
        // TODO: Handle action Foo


        db=mDbHelper.getWritableDatabase();
        if(db==null){
            Log.v("Database2"," unable to get database");
        }
        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++){
                double random=-1;
                while(random<0||random>1.024){
                    random=Math.random();
                }
                if(i>7||j>7){
                    random=0.00001;
                }

                ContentValues values=new ContentValues();

                values.put(AirAzureContract.AirAzureEntry.COLUMN_AIR_INDEX,random*1000);
                values.put(AirAzureContract.AirAzureEntry.COLUMN_TIME,System.currentTimeMillis());
                values.put(AirAzureContract.AirAzureEntry.COLUMN_LONG, 73.023332 + 0.0002 * i);
                values.put(AirAzureContract.AirAzureEntry.COLUMN_LAT, 33.685570 - 0.0002 * j);
                Log.v("Insert", " " + random*1000);
                Log.v("Insert"," "+ (33.685570-0.0002*j));
                Log.v("Insert"," "+ (73.023332+0.0002*i));
                if (db != null) {
                    long newRowId = db.insert(AirAzureContract.AirAzureEntry.TABLE_NAME,null, values);
                    Log.v("Database2", String.valueOf(newRowId));

                }else{
                    Log.v("Database2","Data base reference is null");
                }

                Log.v("RandomValue", "random smog value: " + random * 1000);
            }
        }
    }

    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("AlarmReciever","Enter the AlarmReciever on Recieve Function");
            Intent sendIntent = new Intent(context, AirAzureDownloadService.class);
            sendIntent.putExtra(AirAzureDownloadService.LONGITUDE_LIMIT_LEFT, intent.getStringExtra(AirAzureDownloadService.LONGITUDE_LIMIT_LEFT));
            sendIntent.putExtra(AirAzureDownloadService.LONGITUDE_LIMIT_RIGHT, intent.getStringExtra(AirAzureDownloadService.LONGITUDE_LIMIT_RIGHT));
            sendIntent.putExtra(AirAzureDownloadService.LATITUDE_LIMIT_TOP, intent.getStringExtra(AirAzureDownloadService.LATITUDE_LIMIT_TOP));
            sendIntent.putExtra(AirAzureDownloadService.LATITUDE_LIMIT_BOTTOM, intent.getStringExtra(AirAzureDownloadService.LATITUDE_LIMIT_BOTTOM));
            sendIntent.setAction(intent.getAction());
            context.startService(sendIntent);

        }
    }
}
