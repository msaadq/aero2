package com.aero2.android.DefaultClasses.SQLite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aero2.android.DefaultActivities.Data.AirAzureContract;
import com.aero2.android.DefaultActivities.Data.AirContract;
import com.aero2.android.DefaultClasses.DataTables.ResultDataTable;

import javax.xml.transform.Result;

/**
 * Created by usmankhan on 1/2/2016.
 */
public class ResultsSQLite {

    SQLiteDatabase db;

    public ResultsSQLite(SQLiteDatabase db){
        this.db = db;

    }


    /**
     * Add a new value into local storage.
     * arg: ResultDataTable object
     * exception: None
     * return: None
     */

    public boolean addResultValue(ResultDataTable result){

        ContentValues values=new ContentValues();

        values.put(AirAzureContract.AirAzureEntry.COLUMN_TIME,String.valueOf(result.getTime()));
        values.put(AirAzureContract.AirAzureEntry.COLUMN_LONG,String.valueOf(result.getLong()));
        values.put(AirAzureContract.AirAzureEntry.COLUMN_LAT, String.valueOf(result.getLat()));
        values.put(AirAzureContract.AirAzureEntry.COLUMN_AIR_INDEX, String.valueOf(result.getAirIndex()));

        long newRowId = db.insert(AirAzureContract.AirAzureEntry.TABLE_NAME, null, values);
        Log.v("Row id: ", String.valueOf(newRowId));

        if (newRowId == -1){
            return false;
        }
        else{
            return true;
        }

    }

    /**
     * Delete all values in local storage.
     * arg: None
     * exception: None
     * return: None
     */

    public void emptySQL(){

        db.delete(AirAzureContract.AirAzureEntry.TABLE_NAME, null, null);

    }

    /**
     * Deletes a particular entry in local storage.
     * arg: rowId
     * exception: None
     * return: None
     */

    public void deleteEntry(String rowId){

        String selection = AirAzureContract.AirAzureEntry._ID + "=?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { rowId };
        // Issue SQL statement.
        db.delete(AirAzureContract.AirAzureEntry.TABLE_NAME, selection, selectionArgs);
        Log.v("Deleted", String.valueOf(rowId));
    }

    /**
     * Get all values from local database and return
     * as 2-d double array.
     * arg: None
     * exception: None
     * return: 2-d double array containing row id, time,
     * longitude, latitude, and airquality (in order)
     */

    public Double[][] getAllAirDouble(){

        //Number of columns in table
        int N=5;
        String[] COLUMNS={
                AirAzureContract.AirAzureEntry.TABLE_NAME+"."+ AirAzureContract.AirAzureEntry._ID,
                AirAzureContract.AirAzureEntry.COLUMN_TIME,
                AirAzureContract.AirAzureEntry.COLUMN_LONG,
                AirAzureContract.AirAzureEntry.COLUMN_LAT,
                AirAzureContract.AirAzureEntry.COLUMN_AIR_INDEX
        } ;



        //Get cursor position
        Cursor airCursor = db.query(AirAzureContract.AirAzureEntry.TABLE_NAME, COLUMNS, null,
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
                AirAzureContract.AirAzureEntry.TABLE_NAME+"."+ AirAzureContract.AirAzureEntry._ID
        } ;

        Cursor airCursor = db.query(AirAzureContract.AirAzureEntry.TABLE_NAME, COLUMNS, null,
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
                AirAzureContract.AirAzureEntry.TABLE_NAME+"."+ AirAzureContract.AirAzureEntry._ID
        } ;

        Cursor airCursor = db.query(AirAzureContract.AirAzureEntry.TABLE_NAME, COLUMNS, null,
                null, null, null, null);
        return airCursor.moveToFirst();

    }
}
