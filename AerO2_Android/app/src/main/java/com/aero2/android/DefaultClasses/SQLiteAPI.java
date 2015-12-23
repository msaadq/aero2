package com.aero2.android.DefaultClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.aero2.android.DefaultActivities.Data.AirContract;
import com.aero2.android.DefaultActivities.Data.AirDbHelper;

/**

 * Implementation of SQLite Functions.
 * USAGE:
 *      - Initialize by passing on context.
 *      - Call a function to perform appropriate action.
 *
 * Created by Muddassir on 12/13/2015.
 */

public class SQLiteAPI {

    Context contextLocal;
    AirDbHelper mDbHelper;
    SQLiteDatabase db;

    /**
     * Initializes AirDbHelper and get Database in
     * read format.
     * arg: Context
     * exception: None
     * return: None
     */

    public SQLiteAPI(Context context){

        contextLocal=context;
        mDbHelper = new AirDbHelper(contextLocal);
        db = mDbHelper.getReadableDatabase();

    }

    /**
     * Check whether the device is online.
     * arg: None
     * exception: None
     * return: boolean
     */

    public boolean isOnline() {

        ConnectivityManager cm = (ConnectivityManager)
                contextLocal.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();

    }

    /**
     * Get all values from local database and return
     * as 2-d double array.
     * arg: None
     * exception: None
     * return: 2-d double array containing row id, time,
     * longitude, latitude, altitude & smog (in order)
     */

    public double[][] getAllAirDouble(){

        String[] COLUMNS={
                AirContract.AirEntry.TABLE_NAME+"."+ AirContract.AirEntry._ID,
                AirContract.AirEntry.COLUMN_SMOG_VALUE,
                AirContract.AirEntry.COLUMN_LONG,
                AirContract.AirEntry.COLUMN_LAT,
                AirContract.AirEntry.COLUMN_ALT,
                AirContract.AirEntry.COLUMN_TIME
        } ;

        //Get cursor position
        Cursor airCursor = db.query(AirContract.AirEntry.TABLE_NAME, COLUMNS, null,
                null, null, null, null);

        int noOfRows=airCursor.getCount();
        double[][] allAir=new double[noOfRows][5];

        //Copy values from each row
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

    /**
     * Get all values from local database and return
     * as 2-d string array.
     * arg: None
     * exception: None
     * return: 2-d string array containing row id, time,
     * longitude, latitude, altitude & smog (in order)
     */

    public String[][] getAllAirStrings(){

        String[] COLUMNS={
                AirContract.AirEntry.TABLE_NAME+"."+ AirContract.AirEntry._ID,
                AirContract.AirEntry.COLUMN_SMOG_VALUE,
                AirContract.AirEntry.COLUMN_TIME,
                AirContract.AirEntry.COLUMN_LONG,
                AirContract.AirEntry.COLUMN_LAT,
                AirContract.AirEntry.COLUMN_ALT
        } ;

        //Get cursor position
        Cursor airCursor = db.query(AirContract.AirEntry.TABLE_NAME, COLUMNS, null,
                null, null, null, null);

        int noOfRows=airCursor.getCount();
        String[][] allAir=new String[noOfRows][6];

        //Get values from each row
        for(int i=0;i<noOfRows;i++)
        {
            airCursor.moveToPosition(i);
            for(int j=0;j<6;j++)
            {
                allAir[i][j]=airCursor.getString(j);
            }
        }

        return allAir;

    }

    /**
     * Get number of rows in local database.
     * arg: None
     * exception: None
     * return: Int
     */

    public int getRowCountInLocal(){

        String[] COLUMNS={
                AirContract.AirEntry.TABLE_NAME+"."+ AirContract.AirEntry._ID
        } ;

        Cursor airCursor = db.query(AirContract.AirEntry.TABLE_NAME, COLUMNS, null,
                null, null, null, null);

        int noOfRows=airCursor.getCount();
        return noOfRows;

    }


    /**
     * Check if the local database is empty.
     * arg: None
     * exception: None
     * return: Boolean
     */

    public boolean isLocalEmpty(){

        String[] COLUMNS={
                AirContract.AirEntry.TABLE_NAME+"."+ AirContract.AirEntry._ID
        } ;

        Cursor airCursor = db.query(AirContract.AirEntry.TABLE_NAME, COLUMNS, null,
                null, null, null, null);
        return airCursor.moveToFirst();

    }


    /**
     * Add a new value into local storage.
     * arg: 1-d String array
     * exception: None
     * return: None
     */

    public void addAirValue(String[] params){

        ContentValues values=new ContentValues();

        values.put(AirContract.AirEntry.COLUMN_TIME,params[0]);
        values.put(AirContract.AirEntry.COLUMN_LONG,params[1]);
        values.put(AirContract.AirEntry.COLUMN_LAT,params[2]);
        values.put(AirContract.AirEntry.COLUMN_ALT,params[3]);
        values.put(AirContract.AirEntry.COLUMN_SMOG_VALUE, params[4]);

        long newRowId = db.insert(AirContract.AirEntry.TABLE_NAME,null,values);
        Log.v("Row id: ", String.valueOf(newRowId));

    }


    /**
     * Delete all values in local storage.
     * arg: None
     * exception: None
     * return: None
     */

    public void emptySQL(){

        db.delete(AirContract.AirEntry.TABLE_NAME, null, null);

    }

}
