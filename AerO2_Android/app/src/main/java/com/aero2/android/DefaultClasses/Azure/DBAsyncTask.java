package com.aero2.android.DefaultClasses.Azure;

        import android.app.Activity;
        import android.os.AsyncTask;
        import android.util.Log;

        import com.aero2.android.DefaultClasses.SQLite.SQLiteAPI;

/**
 * Uploads the data to azure.
 * USAGE:
 *      - Initialize DBAsycnTask by passing on activity, DBWriter object &
 *      SQLiteAPI object.
 *      - Call .execute() method and pass on a 2-d array
 *
 *
 * Created by usmankhan on 12/13/2015.
 */
public class DBAsyncTask extends AsyncTask<String[][], Void, Void> {

    Activity activity;
    DBWriter dbWriter;
    SQLiteAPI sqLiteAPI;

    public DBAsyncTask(Activity activity, DBWriter dbWriter, SQLiteAPI sqLiteAPI) {

        this.activity = activity;
        this.dbWriter = dbWriter;
        this.sqLiteAPI = sqLiteAPI;

        Log.v("DBAsyncTask","Instantiated.");
    }

    @Override
    protected Void doInBackground(String[][]... params) {
        Log.v("DBAsyncTask", "Entered doInBackground");

        String[][] integrators = sqLiteAPI.getAllAirStrings();
        int count = sqLiteAPI.getRowCountInLocal();

        //Initialize new 2-d array with 1 less column
        String[][] nIntegrators = new String[count][5];

        //Remove the 'row key' columns
        for (int i=0;i<count;i++){
            for (int j=0;j<5;j++){

                nIntegrators[i][j] = integrators[i][j + 1];
                Log.v("nInegrator: ",nIntegrators[i][j]);
            }
        }

        //Add item in mobile activity
        for (int i = 0; i < count; i++) {

            String rowId = integrators[i][0];
            dbWriter.addItem(null, nIntegrators[i], rowId, sqLiteAPI);
            Log.v("DBAsyncTask", "Added Item " + String.valueOf(i));

        }

        return null;
    }

}