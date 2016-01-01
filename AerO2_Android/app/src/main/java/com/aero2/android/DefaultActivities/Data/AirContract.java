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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the work and hobbie database.
 */
public class AirContract {
	//To Prevent Someone from accidentally instantiating the contract 
	// giving it an empty contructor

    public static final int IS_A_JOB=0;
    public static final int IS_A_HOBBIE=1;

    public static final int IS_INCOMPLETE=0;
    public static final int IS_COMPLETE=1;
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.aero2.android.DefaultActivities";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //this path will be added to the base uri, so that we can get to the work table in the SQLite Database
    public static final String PATH_AIR = "air";


    //Inner Class that defines the contest of the Work DataBase
    public static final class AirEntry implements BaseColumns {

     //the path needed to get to the work table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_AIR).build();

     //type of a set of tuples from the database
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_AIR;

     //type of one tuple from the database
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_AIR;

        // Table name
        public static final String TABLE_NAME = "air";

        //smog value
        public static final String COLUMN_SMOG_VALUE = "smog_value_of_air";

        //air quality
        public static final String COLUMN_NORMALIZED="normalized";

        //time at which the value was taken
        public static final String COLUMN_TIME = "time_of_air";

        //Longitude at which the value was taken
        public static final String COLUMN_LONG = "long_of_air";

        //Latitude at which the value was taken
        public static final String COLUMN_LAT = "lat_of_air";

        //Altitude at which the value was taken
        public static final String COLUMN_ALT = "alt_of_air";

        //returns uri for the work table
        public static Uri buildAirUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        //name of task is returned
        public static String getTimeOfAirFromUri(Uri uri) {
            String TimeOfAir=uri.getPathSegments().get(1);
            return TimeOfAir;
        }

        public static String getLongOfAirFromUri(Uri uri){
            String LongOfAir=uri.getPathSegments().get(2);
            return LongOfAir;
        }

        public static String getLatOfAirFromUri(Uri uri){
            String LatOfAir=uri.getPathSegments().get(3);
            return LatOfAir;
        }

        public static String getAltOfAirFromUri(Uri uri){
            String AltOfAir=uri.getLastPathSegment();
            return AltOfAir;
        }


        //making a specific uri
        public static Uri buildAirValue(String value) {
            return CONTENT_URI.buildUpon().appendPath(value).build();
        }

        //making a uri with time and coordinates
        public static Uri buildAirUriFromCoordinatesandTime(double time,double longi,double lat, double alt){
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(time))
                    .appendPath(String.valueOf(longi))
                    .appendPath(String.valueOf(lat))
                    .appendPath(String.valueOf(alt)).build();
        }

        //making a uri with time
        public static Uri buildUriFromTime(double time){
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(time)).build();
        }

        //making a uri with Coordinates
        public static Uri buildUriFromCoordinates(double longi,double lat,double alt){
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(longi))
                    .appendPath(String.valueOf(lat))
                    .appendPath(String.valueOf(alt)).build();
        }

        public static String getLatFromCoordinateUri(Uri uri) {
            String Lat= uri.getPathSegments().get(2);
            return Lat;
        }

        public static String getLongFromCoordinatesUri(Uri uri) {
            String Long=uri.getPathSegments().get(1);
            return Long;
        }

        public static String getAltFromCoordinateUri(Uri uri) {
            String Alt=uri.getPathSegments().get(3);
            return Alt;
        }

        public static String getTimeFromTimeUri(Uri uri) {
            String Time=uri.getLastPathSegment();
            return Time;

        }
    }
}
