package com.aero2.android.DefaultActivities.Data;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class AirAzureDownloadService extends IntentService {

    //TODO add download code in the handleActionDownloadAzureAir Data

    public static final String DOWNLOAD_AZURE_AIR_DATA = "com.aero2.android.DefaultActivities.Data.download.AZUREAIRDATA";
    public static final String UPDATE_LOCAL_CACHE = "com.aero2.android.DefaultActivities.Data.update.LOCALCACHE";


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




    // TODO: Customize helper method
    public void startActionDownloadAzureAirData(Context context, String latLimitTop, String latLimitBottom, String longLimitLeft, String longLimitRight) {
        Log.v("StartAction","Staritng Download Action");
        localContext=context;

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




    private void handleActionDownloadAzureAirData(String latLimitTop, String latLimitBottom,String longLimitLeft,String longLimitRight) {

        // /TODO Implement azure results download code here

        //This code inserts random values in the localResults cache
        //Starts Here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        mDbHelper=new AirAzureDbHelper(getApplicationContext());

        db=mDbHelper.getWritableDatabase();
        if(db==null){
            Log.v("Database2"," unable to get database");
        }
        for(int i=0;i<50;i++){
            for(int j=0;j<50;j++){
                double random=-1;
                while(random<0||random>1.024){
                    random=Math.random();
                }
                if(i>7||j>7){
                    random=0.00001;
                }
                //Local Cache insert
                //Starts Here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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
                //Local Cache insert
                //ENDS HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                Log.v("RandomValue", "random smog value: " + random * 1000);
            }
        }
        //This code inserts random values in the localResults cache
        //Ends Here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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
