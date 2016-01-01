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
package com.aero2.android.data;

import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.aero2.android.DefaultActivities.Data.AirContract;

/*
    Students: This is NOT a complete test for the WeatherContract --- just for the functions
    that we expect you to write.
 */
public class TestWeatherContract extends AndroidTestCase {

    // intentionally includes a slash to make sure Uri is getting quoted correctly
    private static final String TEST_AIR_VALUE = "/222 445 333";
    private static final long TEST_WEATHER_DATE = 1419033600L;  // December 20th, 2014


    public void testBuildWorkName() {

        Uri locationUri = AirContract.AirEntry.buildAirValue(TEST_AIR_VALUE);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildWeatherLocation in " +
                        "WeatherContract.",
                locationUri);
        assertEquals("Error: Weather location not properly appended to the end of the Uri",
                TEST_AIR_VALUE, locationUri.getLastPathSegment());
        Log.v("URI",locationUri.toString());
        assertEquals("Error: Weather location Uri doesn't match our expected result",
                locationUri.toString(),
                "content://com.aero2.android.DefaultActivities/air/%2F222%20445%20333");
    }
}
