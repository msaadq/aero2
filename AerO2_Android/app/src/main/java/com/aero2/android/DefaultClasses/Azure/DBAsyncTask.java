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
public class DBAsyncTask extends AsyncTask<Double[][], Void, Void> {

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
    protected Void doInBackground(Double[][]... params) {

        //Number of parameters in integrator array
        int N=6;

        Log.v("DBAsyncTask", "Entered doInBackground");

        Double[][] integrators = sqLiteAPI.getAllAirDouble();
        int count = sqLiteAPI.getRowCountInLocal();

        //Initialize new 2-d array with 1 less column
        Double[][] nIntegrators = new Double[count][N];

        //Remove the 'row key' columns
        for (int i=0;i<count;i++){
            for (int j=0;j<N;j++){

                nIntegrators[i][j] = integrators[i][j + 1];
                Log.v("nInegrator: ",String.valueOf(nIntegrators[i][j]));
            }
        }

        //Add item in mobile activity
        for (int i = 0; i < count; i++) {

            String rowId = String.valueOf(integrators[i][0]);
            dbWriter.addItem(null, nIntegrators[i], rowId, sqLiteAPI);
            Log.v("DBAsyncTask", "Added Item " + String.valueOf(i));

        }

        return null;
    }

}