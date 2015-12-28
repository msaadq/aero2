package com.aero2.android.DefaultClasses;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.aero2.android.DefaultActivities.MainActivity;

/**
 * Calls SQLiteAPI and upload data in local storage.
 * USAGE:
 *      - Initialize SQLiteAsycnTask by passing on activity &
 *      SQLiteAPI object.
 *      - Call .execute() method and pass on a 1-d array
 *
 *
 * Created by usmankhan on 12/13/2015.
 */
public class SQLiteAsyncTask extends AsyncTask<String[][], Void, Void> {

    Activity activity;
    SQLiteAPI sqLiteAPI;

    public SQLiteAsyncTask(Activity activity, SQLiteAPI sqLiteAPI) {  // can take other params if needed

        this.activity = activity;
        this.sqLiteAPI = sqLiteAPI;
        Log.v("SQLiteAsyncTask", "Instantiated.");

    }

    @Override
    protected Void doInBackground(String[][]... params) {

        Log.v("SQLiteAsyncTask", "Entered doInBackground");
        int count  = Integrator.value_count;
        Log.v("Count:",String.valueOf(count));

        //Push all values to SQLite
        for (int i=0; i< count; i++) {
            sqLiteAPI.addAirValue(params[0][i]);
            Log.v("SQLiteAsyncTask", "Added Item " + String.valueOf(i));
        }

        Integrator.value_count = 0;
        long row_count = sqLiteAPI.getRowCountInLocal();
        Log.v("Row Count: ",String.valueOf(row_count));

        return null;

    }

}
