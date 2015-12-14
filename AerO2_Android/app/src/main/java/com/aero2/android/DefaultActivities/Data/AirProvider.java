/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aero2.android.DefaultActivities.Data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class AirProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private AirDbHelper mOpenHelper;

    //complete work of the user
    public static final int ALL_AIR = 100;

    public static final int AIR_BY_TIME=101;

    public static final int AIR_BY_COORD=102;

    public static final int AIR_BY_TIME_AND_COORD=103;

    




    //an instance of the SQLiteQueryBuilder Class used to query the work table
    private static final SQLiteQueryBuilder AirQueryBuilder;


    static{
        AirQueryBuilder = new SQLiteQueryBuilder();
        AirQueryBuilder.setTables(AirContract.AirEntry.TABLE_NAME);
    }


    private static final String AirSelectionByCoordAndTime =
            AirContract.AirEntry.TABLE_NAME+
                    "." + AirContract.AirEntry.COLUMN_TIME + " =? AND "
                    +AirContract.AirEntry.COLUMN_LONG+" =? AND"
                    +AirContract.AirEntry.COLUMN_LAT+" =? AND"
                    +AirContract.AirEntry.COLUMN_ALT+" =? ";

    private static final String AirSelectionByCoord=
            AirContract.AirEntry.TABLE_NAME+
                    "." +AirContract.AirEntry.COLUMN_LONG+" =? AND"
                    +AirContract.AirEntry.COLUMN_LAT+" =? AND"
                    +AirContract.AirEntry.COLUMN_ALT+" =? ";

    private static final String AirSelectionByTime=
            AirContract.AirEntry.TABLE_NAME+
                    "." +AirContract.AirEntry.COLUMN_TIME+" =? ";


    //used for a detailed view of a specific task from the work table
    private Cursor getAirByCoordinatesAndTime(Uri uri, String[] projection, String sortOrder) {
        String TimeOfAir = AirContract.AirEntry.getTimeOfAirFromUri(uri);
        String LongOfAir = AirContract.AirEntry.getLongOfAirFromUri(uri);
        String LatOfAir = AirContract.AirEntry.getLatOfAirFromUri(uri);
        String AltOfAir = AirContract.AirEntry.getAltOfAirFromUri(uri);
        String[] selectionArgs;
        String selection;

            selection = AirSelectionByCoordAndTime;
            selectionArgs = new String[]{TimeOfAir,LongOfAir,LatOfAir,AltOfAir};
            

        return AirQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
    private Cursor getAirByCoordinates(Uri uri,String[] projection,String sortOrder){
        String latOfAir=AirContract.AirEntry.getLatFromCoordinateUri(uri);
        String longOfAir= AirContract.AirEntry.getLongFromCoordinatesUri(uri);
        String altOfAir=AirContract.AirEntry.getAltFromCoordinateUri(uri);
        String[] selectionArgs;
        String selection;

        selection = AirSelectionByCoord;
        selectionArgs = new String[]{longOfAir,latOfAir,altOfAir};


        return AirQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getAirByTime(Uri uri,String[] projection,String sortOrder){
        String time=AirContract.AirEntry.getTimeFromTimeUri(uri);
        String[] selectionArgs;
        String selection;

        selection = AirSelectionByTime;
        selectionArgs = new String[]{time};


        return AirQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
    //used to get all the tuples of the work table
    private Cursor getAllAir(String[] projection,String sortOrder) {
        return AirQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }
    
    

    

    //used to build two types of uris, One to get all the the tasks in the work table and one to get a specific tuple for a detailed view
    public static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AirContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, AirContract.PATH_AIR, ALL_AIR);
        matcher.addURI(authority, AirContract.PATH_AIR + "/*", AIR_BY_TIME);
        matcher.addURI(authority,AirContract.PATH_AIR+"/*/*/*",AIR_BY_COORD);
        matcher.addURI(authority,AirContract.PATH_AIR+"/*/*/*/*",AIR_BY_TIME_AND_COORD);
        return matcher;
    }

    /*
        Students: We've coded this for you.  We just create a new WeatherDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new AirDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.

     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case ALL_AIR:
                return AirContract.AirEntry.CONTENT_TYPE;
            case AIR_BY_TIME:
                return AirContract.AirEntry.CONTENT_TYPE;
            case AIR_BY_COORD:
                return AirContract.AirEntry.CONTENT_TYPE;
            case AIR_BY_TIME_AND_COORD:
                return AirContract.AirEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "work"
            case ALL_AIR:
            {
                retCursor = getAllAir(projection,sortOrder);
                break;
            }
            // "work/*"
            case AIR_BY_TIME: {
                retCursor = getAirByTime(uri, projection, sortOrder);
                break;
            }
            case AIR_BY_COORD:{
                retCursor=getAirByCoordinates(uri,projection,sortOrder);
                break;
            }
            case AIR_BY_TIME_AND_COORD:{
                retCursor=getAirByCoordinatesAndTime(uri,projection,sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    //for inserting data into the database
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case ALL_AIR: {
                long _id = db.insert(AirContract.AirEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = AirContract.AirEntry.buildAirUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    //for deleting data from the work tables
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case ALL_AIR:{
                rowsDeleted = db.delete(
                        AirContract.AirEntry.TABLE_NAME, selection, selectionArgs);
                break;}
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    //update rows in the Work Table
    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case ALL_AIR:{
                rowsUpdated = db.update(AirContract.AirEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;}
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    //inset multiple tuples at the same time
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ALL_AIR:{
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(AirContract.AirEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
