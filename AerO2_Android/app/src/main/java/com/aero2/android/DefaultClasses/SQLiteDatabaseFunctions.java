package com.aero2.android.DefaultClasses;

import android.app.Activity;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.aero2.android.DefaultActivities.Data.AirContract;

/**
 * Created by Muddassir on 12/13/2015.
 */

public class SQLiteDatabaseFunctions extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    Integrator integrator;
    DBWriter dbWriterLocal;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {

                //Every 2 seconds adding a new value to the database

                long endTime = System.currentTimeMillis() + 2 * 1000;
                while (System.currentTimeMillis() < endTime) {
                    synchronized (this) {
                        try {
                            wait(endTime - System.currentTimeMillis());
                        } catch (Exception e) {
                        }
                    }
                }

                double[] airTuple=integrator.integrateSmog();
                addAirValue(airTuple[0], airTuple[1], airTuple[2], airTuple[3], airTuple[4]);
                if(isOnline()){
                    uploadAir(dbWriterLocal);
                }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting...Recording Smog Values every 2 seconds", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "stop recording values, service closed", Toast.LENGTH_SHORT).show();
    }

    public class LocalBinder extends Binder implements IActivityCallingService {
        public SQLiteDatabaseFunctions getService() {
            return SQLiteDatabaseFunctions.this;
        }

        @Override
        public void StartListenActivity(Activity activity) {
            integrator=new Integrator(activity);
            dbWriterLocal=new DBWriter(activity);
        }
    }

    private final IBinder mBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public interface IActivityCallingService {
        void StartListenActivity(Activity activity);

    }

    //Function to check wether the device is online

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /*****************************************************************************************/
    /* -----------------FUNCTIONS FOR INTERACTION WITH LOCAL DATA BASE-----------------------*/
    /*****************************************************************************************/

    //get all the values in the local database
    public double[][] getAllAir(){

        String[] COLUMNS={
                AirContract.AirEntry.TABLE_NAME+"."+ AirContract.AirEntry._ID,
                AirContract.AirEntry.COLUMN_SMOG_VALUE,
                AirContract.AirEntry.COLUMN_TIME,
                AirContract.AirEntry.COLUMN_LONG,
                AirContract.AirEntry.COLUMN_LAT,
                AirContract.AirEntry.COLUMN_ALT
        } ;

        Cursor airCursor=getContentResolver().query(AirContract.AirEntry.CONTENT_URI,COLUMNS,null,null,null);
        int noOfRows=airCursor.getCount();
        double[][] allAir=new double[noOfRows][5];
        for(int i=0;i<noOfRows;i++)
        {
            airCursor.moveToPosition(i);
            for(int j=0;j<5;j++)
            {
                allAir[i][j]=Double.valueOf(airCursor.getString(j));
            }
        }
        return allAir;
    }

    //get all air values at a certain Coordinate
    public double[][] getAllAirAtCoordinates(double longi,double lat,double alt){
        String[] COLUMNS={
                AirContract.AirEntry.TABLE_NAME+"."+ AirContract.AirEntry._ID,
                AirContract.AirEntry.COLUMN_SMOG_VALUE,
                AirContract.AirEntry.COLUMN_TIME,
                AirContract.AirEntry.COLUMN_LONG,
                AirContract.AirEntry.COLUMN_LAT,
                AirContract.AirEntry.COLUMN_ALT
        } ;
        Cursor airCursor=getContentResolver().query(AirContract.AirEntry.buildUriFromCoordinates(longi, lat, alt),COLUMNS,null,null,null);
        int noOfRows=airCursor.getCount();
        double[][] allAirAtCoordinates=new double[noOfRows][5];
        for(int i=0;i<noOfRows;i++)
        {
            airCursor.moveToPosition(i);
            for(int j=0;j<5;j++)
            {
                allAirAtCoordinates[i][j]=Double.valueOf(airCursor.getString(j));
            }
        }
        return allAirAtCoordinates;

    }

    //get All the Smog Values at a certain time
    public double[][] getAllAirAtTime(double time){
        String[] COLUMNS={
                AirContract.AirEntry.TABLE_NAME+"."+ AirContract.AirEntry._ID,
                AirContract.AirEntry.COLUMN_SMOG_VALUE,
                AirContract.AirEntry.COLUMN_TIME,
                AirContract.AirEntry.COLUMN_LONG,
                AirContract.AirEntry.COLUMN_LAT,
                AirContract.AirEntry.COLUMN_ALT
        } ;
        Cursor airCursor=getContentResolver().query(AirContract.AirEntry.buildUriFromTime(time),COLUMNS,null,null,null);
        int noOfRows=airCursor.getCount();
        double[][] allAirAtTime=new double[noOfRows][5];
        for(int i=0;i<noOfRows;i++)
        {
            airCursor.moveToPosition(i);
            for(int j=0;j<5;j++)
            {
                allAirAtTime[i][j]=Double.valueOf(airCursor.getString(j));
            }
        }
        return allAirAtTime;

    }

    //get value of smog at a certain time and coordinates
    public double getAirByTimeAndCoordinates(double time,double longi, double lat, double alt){
        double smogValue;
        String[] COLUMNS={
                AirContract.AirEntry.COLUMN_SMOG_VALUE
        } ;
        Uri uri= AirContract.AirEntry.buildAirUriFromCoordinatesandTime(time, longi, lat, alt);
        Cursor airCursor=getContentResolver().query(uri, COLUMNS, null, null, null);
        smogValue=Double.valueOf(airCursor.getString(0));
        return smogValue;
    }

    //add a new entry into the table
    public void addAirValue(double smogValue,double time,double longi,double lat,double alt){
        ContentValues values=new ContentValues();
        values.put(AirContract.AirEntry.COLUMN_SMOG_VALUE,String.valueOf(smogValue));
        values.put(AirContract.AirEntry.COLUMN_TIME,String.valueOf(time));
        values.put(AirContract.AirEntry.COLUMN_LONG,String.valueOf(longi));
        values.put(AirContract.AirEntry.COLUMN_LAT,String.valueOf(lat));
        values.put(AirContract.AirEntry.COLUMN_ALT,String.valueOf(alt));
        getContentResolver().insert(AirContract.AirEntry.CONTENT_URI, values);
    }

    //upload all air values into the online database and empty the local database
    public void uploadAir(DBWriter dbWriter){
        double[][] allAir=getAllAir();
        getContentResolver().delete(AirContract.AirEntry.CONTENT_URI, null, null);
        for(int i=0;i<allAir.length;i++)
            dbWriter.addItem(String.valueOf(allAir[i][1]),allAir[i]);
    }

    /***************************************************************************/
    /*----------------------------------END------------------------------------*/
    /***************************************************************************/

}
