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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for weather data.
 */
public class AirAzureDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 5;

    public static final String DATABASE_NAME = "airazure.db";

    public AirAzureDbHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Creating the Work Table
        //has one _ID column and six other columns for various functions of the app
        final String SQL_CREATE_AIR_AZURE_TABLE = "CREATE TABLE " + AirAzureContract.AirAzureEntry.TABLE_NAME + " (" +
                AirAzureContract.AirAzureEntry._ID + " INTEGER PRIMARY KEY," +
                AirAzureContract.AirAzureEntry.COLUMN_AIR_INDEX+" TEXT NOT NULL, "+
                AirAzureContract.AirAzureEntry.COLUMN_TIME + " TEXT UNIQUE NOT NULL, " +
                AirAzureContract.AirAzureEntry.COLUMN_LONG + " TEXT NOT NULL, " +
                AirAzureContract.AirAzureEntry.COLUMN_LAT + " TEXT NOT NULL " +
                " );";


        sqLiteDatabase.execSQL(SQL_CREATE_AIR_AZURE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AirAzureContract.AirAzureEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
