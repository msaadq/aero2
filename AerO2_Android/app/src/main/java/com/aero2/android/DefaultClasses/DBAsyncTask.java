package com.aero2.android.DefaultClasses;

        import android.app.Activity;
        import android.os.AsyncTask;
        import android.util.Log;

        import com.aero2.android.DefaultActivities.MainActivity;

/**
 * Instantiates STMCommunicator which in turn creates a BTService
 * object to connect application with smog sensor.
 *
 * Created by usmankhan on 12/13/2015.
 */
public class DBAsyncTask extends AsyncTask<String[][], Void, Void> {

    Activity activity;
    DBWriter dbWriter;

    public DBAsyncTask(Activity activity, DBWriter dbWriter) {  // can take other params if needed
        this.activity = activity;
        this.dbWriter = dbWriter;
        Log.v("DBAsyncTask","Instantiated.");
    }

    @Override
    protected Void doInBackground(String[][]... params) {
        Log.v("DBAsyncTask", "Entered doInBackground");
        int count  = MainActivity.value_count;

        for (int i=0; i< count; i++) {
            dbWriter.addItem(params[0][i][0], params[0][i]);
            Log.v("DBAsyncTask", "Added Item " + String.valueOf(i));
        }

        return null;
    }

}