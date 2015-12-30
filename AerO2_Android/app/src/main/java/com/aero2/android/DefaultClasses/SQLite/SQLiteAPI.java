package com.aero2.android.DefaultClasses.SQLite;

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
     * longitude, latitude, altitude, smog & normalized (in order)
     */

    public Double[][] getAllAirDouble(){

        //Number of columns in table
        int N=7;
        String[] COLUMNS={
                AirContract.AirEntry.TABLE_NAME+"."+ AirContract.AirEntry._ID,
                AirContract.AirEntry.COLUMN_TIME,
                AirContract.AirEntry.COLUMN_LONG,
                AirContract.AirEntry.COLUMN_LAT,
                AirContract.AirEntry.COLUMN_ALT,
                AirContract.AirEntry.COLUMN_SMOG_VALUE,
                AirContract.AirEntry.COLUMN_NORMALIZED
        } ;

        //Get cursor position
        Cursor airCursor = db.query(AirContract.AirEntry.TABLE_NAME, COLUMNS, null,
                null, null, null, null);

        int noOfRows=airCursor.getCount();
        Double[][] allAir = new Double[noOfRows][N];

        //Copy values from each row
        for(int i=0;i<noOfRows;i++)
        {
            airCursor.moveToPosition(i);
            for(int j=0;j<N;j++)
            {
                allAir[i][j]=Double.valueOf(airCursor.getString(j));
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

    public void addAirValue(Double[] params){

        ContentValues values=new ContentValues();

        values.put(AirContract.AirEntry.COLUMN_TIME,String.valueOf(params[0]));
        values.put(AirContract.AirEntry.COLUMN_LONG,String.valueOf(params[1]));
        values.put(AirContract.AirEntry.COLUMN_LAT,String.valueOf(params[2]));
        values.put(AirContract.AirEntry.COLUMN_ALT,String.valueOf(params[3]));
        values.put(AirContract.AirEntry.COLUMN_SMOG_VALUE, String.valueOf(params[4]));
        values.put(AirContract.AirEntry.COLUMN_NORMALIZED, String.valueOf(params[5]));

        long newRowId = db.insert(AirContract.AirEntry.TABLE_NAME, null, values);
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

    public void deleteEntry(String rowId){

        String selection = AirContract.AirEntry._ID + "=?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { rowId };
        // Issue SQL statement.
        db.delete(AirContract.AirEntry.TABLE_NAME, selection, selectionArgs);
        Log.v("Deleted",String.valueOf(rowId));
    }

}