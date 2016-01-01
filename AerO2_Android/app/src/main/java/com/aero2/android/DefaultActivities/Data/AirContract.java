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

import android.provider.BaseColumns;

/**
 * Defines table and column names for the work and hobbie database.
 */
public class AirContract {

    //Inner Class that defines the contest of the Work DataBase
    public static final class AirEntry implements BaseColumns {

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


    }
}
