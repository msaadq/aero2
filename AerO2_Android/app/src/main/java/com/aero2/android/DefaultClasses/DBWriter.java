package com.aero2.android.DefaultClasses;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

/**
 * The class for writing Rows into the Azure Database
 * Default Usage Guide:
 *      in onCreate method, create an object. i.e. DBWriter dbWriter = new DBWriter(this);
 *      use dbWriter.write(double[] array) to save Double arrays into the database.
 *
 * Created by Saad on 12/8/2015.
 */

public class DBWriter {

    public final static String DEFAULT_URL = "https://aero2.azure-mobile.net/";
    public final static String DEFAULT_KEY = "WWnbNVGsdqRoniOxupcfwjOQaosAAl67";

    private MobileServiceClient mClient;  // Mobile Service Client reference
    private MobileServiceTable<SampleDataTable> mTable;  // Mobile Service Table used to access data


    /**
     * Default Constructor
     * This calls the parametrized constructor with default values
     * arg: The Current Activity
     * exception: None
     * return: No return value.
     */

    public DBWriter(Activity activity) {
        this(activity, DEFAULT_URL, DEFAULT_KEY);
    }

    /**
     * Parametrized Constructor
     * This calls the parametrized constructor with provided URL and Key
     * arg: The Current Activity, Url, Key
     * exception: None
     * return: No return value.
     */

    public DBWriter(Activity activity, String url, String key) {
        try {

            Log.d("App Status", "Entered Function");

            // Create the Mobile Service Client instance, using the provided URL and key
            mClient = new MobileServiceClient(url, key, activity);

            Log.d("App Status", "URL Successful");

            // Get the Mobile Service Table instance to use
            mTable = mClient.getTable(SampleDataTable.class);

            Log.d("App Status", "Table get successful");

        } catch (MalformedURLException e) {
            Log.d("Incorrect URL", "Error");
        } catch (Exception e){
            Log.d("Exception", "Unknown Error with URL");
        }
    }

    /**
     * Adds a new Row to the database
     * arg: Double array
     * exception: None
     * return: No return value.
     */

    public void addItem(String id, double[] data) {
        if (mClient == null) {
            return;
        }
        // Create a new item
        final SampleDataTable mSampleDataTable = new SampleDataTable(id, data);

        // Insert the new item
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    addItemInTable(mSampleDataTable);
                    Log.v("DBWriter","Data Saved");
                } catch (final Exception e) {
                    Log.d("Exception", "Data Cannot be saved");
                }
                return null;
            }
        };

        runAsyncTask(task);
    }

    /**
     * Adds a compatible List SampleDataTable to the Database table
     * arg: DBListItem
     * exception: ExecutionException, InterruptedException
     * return: No return value.
     */

    private SampleDataTable addItemInTable(SampleDataTable sampleDataTable) throws ExecutionException, InterruptedException {
        SampleDataTable entity = mTable.insert(sampleDataTable).get();
        return entity;
    }

    /**
     * Executes the AsyncTask
     * arg: AsyncTask
     * exception: None
     * return: No return value.
     */

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        return task.execute();
    }
}
