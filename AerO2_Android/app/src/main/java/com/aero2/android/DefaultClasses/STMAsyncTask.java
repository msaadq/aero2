package com.aero2.android.DefaultClasses;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

/**
 * Instantiates STMCommunicator which in turn creates a BTService
 * object to connect application with smog sensor.
 *
 * Created by usmankhan on 12/13/2015.
 */
public class STMAsyncTask extends AsyncTask<Void, Void, Void> {

    STMCommunicator stmCommunicator;
    Activity activity;

    public STMAsyncTask(Activity activity) {  // can take other params if needed
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            stmCommunicator = new STMCommunicator(activity);
        } catch (IOException e) {
            Log.d("Exception", "STMCommunicator object could not be created.");
        }
        return null;
    }


}
