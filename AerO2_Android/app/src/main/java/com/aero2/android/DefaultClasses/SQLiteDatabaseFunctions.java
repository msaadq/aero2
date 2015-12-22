package com.aero2.android.DefaultClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.aero2.android.DefaultActivities.Data.AirContract;

import java.util.concurrent.ExecutionException;

/**

 * Created by Muddassir on 12/13/2015.
 */

public class SQLiteDatabaseFunctions {

    Context contextLocal;
    public SQLiteDatabaseFunctions(Context context){

        contextLocal=context;
    }


    //Function to check whether the device is online
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) contextLocal.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /*****************************************************************************************/
    /* -----------------FUNCTIONS FOR INTERACTION WITH LOCAL DATA BASE-----------------------*/
    /*****************************************************************************************/

    //get all the values in the local database in the form of doubles
    public double[][] getAllAirDouble(){

        String[] COLUMNS={
                AirContract.AirEntry.TABLE_NAME+"."+ AirContract.AirEntry._ID,
                AirContract.AirEntry.COLUMN_SMOG_VALUE,
                AirContract.AirEntry.COLUMN_TIME,
                AirContract.AirEntry.COLUMN_LONG,
                AirContract.AirEntry.COLUMN_LAT,
                AirContract.AirEntry.COLUMN_ALT
        } ;

        Cursor airCursor=contextLocal.getContentResolver().query(AirContract.AirEntry.CONTENT_URI,COLUMNS,null,null,null);
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


    //get all the values in the local database in the form of Strings
    public String[][] getAllAirStrings(){
        String[] COLUMNS={
                AirContract.AirEntry.TABLE_NAME+"."+ AirContract.AirEntry._ID,
                AirContract.AirEntry.COLUMN_SMOG_VALUE,
                AirContract.AirEntry.COLUMN_AIR_QUALITY,
                AirContract.AirEntry.COLUMN_TIME,
                AirContract.AirEntry.COLUMN_LONG,
                AirContract.AirEntry.COLUMN_LAT,
                AirContract.AirEntry.COLUMN_ALT
        } ;

        Cursor airCursor=contextLocal.getContentResolver().query(AirContract.AirEntry.CONTENT_URI,COLUMNS,null,null,null);
        int noOfRows=airCursor.getCount();
        String[][] allAir=new String[noOfRows][6];
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

    //get number of rows in the local database
    public int getRowCountInLocal(){
        String[] COLUMNS={
                AirContract.AirEntry.TABLE_NAME+"."+ AirContract.AirEntry._ID
        } ;

        Cursor airCursor=contextLocal.getContentResolver().query(AirContract.AirEntry.CONTENT_URI, COLUMNS, null, null, null);
        int noOfRows=airCursor.getCount();
        return noOfRows;
    }

    //find out if the local data base is empty
    public boolean isLocalEmpty(){
        String[] COLUMNS={
                AirContract.AirEntry.TABLE_NAME+"."+ AirContract.AirEntry._ID
        } ;

        Cursor airCursor=contextLocal.getContentResolver().query(AirContract.AirEntry.CONTENT_URI,COLUMNS,null,null,null);
        return airCursor.moveToFirst();
    }


    //add a new entry into the table
    public void addAirValue(SampleDataTable sampleDataTable){
        ContentValues values=new ContentValues();
        values.put(AirContract.AirEntry.COLUMN_SMOG_VALUE,sampleDataTable.getSmog());
        values.put(AirContract.AirEntry.COLUMN_AIR_QUALITY,sampleDataTable.getmNormalized());
        values.put(AirContract.AirEntry.COLUMN_TIME,sampleDataTable.getTime());
        values.put(AirContract.AirEntry.COLUMN_LONG,sampleDataTable.getLong());
        values.put(AirContract.AirEntry.COLUMN_LAT,sampleDataTable.getLat());
        values.put(AirContract.AirEntry.COLUMN_ALT,sampleDataTable.getAlt());
        contextLocal.getContentResolver().insert(AirContract.AirEntry.CONTENT_URI, values);
    }

    //upload all air values into the online database and empty the local database
    public void emptySQL(){

        contextLocal.getContentResolver().delete(AirContract.AirEntry.CONTENT_URI, null, null);

    }

    /***************************************************************************/
    /*----------------------------------END------------------------------------*/
    /***************************************************************************/

}
