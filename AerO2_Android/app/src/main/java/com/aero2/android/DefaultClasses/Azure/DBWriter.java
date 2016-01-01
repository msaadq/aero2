package com.aero2.android.DefaultClasses.Azure;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.aero2.android.DefaultClasses.DataTables.ResultDataTable;
import com.aero2.android.DefaultClasses.DataTables.SampleDataTable;
import com.aero2.android.DefaultClasses.SQLite.SQLiteAPI;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
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
    private MobileServiceTable<SampleDataTable> sTable;  // Mobile Service Table used to access data
    private MobileServiceTable<ResultDataTable> rTable;


    /**
     * Default Constructor
     * This calls the parametrized constructor with default values
     * arg: The Current Activity
     * exception: None
     * return: No return value.
     */

    public DBWriter(Activity activity, Class table) {
        this(activity, table, DEFAULT_URL, DEFAULT_KEY);
    }


    /**
     * Parametrized Constructor
     * This calls the parametrized constructor with provided URL and Key
     * arg: The Current Activity, Url, Key
     * exception: None
     * return: No return value.
     */

    public DBWriter(Activity activity, Class table, String url, String key) {
        try {

            // Create the Mobile Service Client instance, using the provided URL and key
            mClient = new MobileServiceClient(url, key, activity);
            Log.d("DBWriter", "URL Successful");

            // Get the Mobile Service Table instance to use
            if (table == SampleDataTable.class) {
                sTable = mClient.getTable(table);
            }
            else if (table == ResultDataTable.class){
                rTable = mClient.getTable(table);
            }

            Log.d("DBWriter", "Table get successful");

        } catch (MalformedURLException e) {
            Log.d("Incorrect URL", "Error");
        } catch (Exception e){
            Log.d("Exception", "Unknown Error with URL");
        }
    }

    /**
     * Adds a new Row to the database
     * arg: Double array
     * exception: ExecutionException & InterruptedException
     * return: No return value.
     */

    public void addItem(String id, Double[] data) throws ExecutionException, InterruptedException {
        if (mClient == null) {
            return;
        }

        // Create a new item
        final SampleDataTable mSampleDataTable = new SampleDataTable(id, data);

        // Insert the new item
        try {
            sTable.insert(mSampleDataTable);
            Log.e("DBWriter", "Data Saved");

        } catch (final Exception e) {
            Log.e("Exception", "Data Cannot be saved");
        }

    }


    public void getItems() throws ExecutionException {
        if (mClient == null) {
            return;
        }

        try {
            Log.v("DBWriter retrieve", "Starting.");
            final MobileServiceList<ResultDataTable> result = rTable.where().field("time").eq("123456.123456").execute().get();
            Log.v("DBWriter retrieve","Data Captured.");
            for (ResultDataTable item:result){
                Log.v("Output:",String.valueOf(item.getLat()));
            }

        }
        catch (Exception e){
            Log.v("DBWriter","Exception reading values.");
        }
    }


}
