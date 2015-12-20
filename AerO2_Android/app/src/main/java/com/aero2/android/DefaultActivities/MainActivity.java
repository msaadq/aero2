package com.aero2.android.DefaultActivities;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.aero2.android.DefaultActivities.Data.AirContract;
import com.aero2.android.DefaultClasses.AirAdapter;
import com.aero2.android.DefaultClasses.DBWriter;
import com.aero2.android.DefaultClasses.GPSTracker;
import com.aero2.android.DefaultClasses.SQLiteDatabaseFunctions;
import com.aero2.android.R;

import java.io.IOException;


public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{


    // Time between each GPS recording
    private int m_interval = 1000;
    private int value_count = 0;
    private final int max_value_count = 1000;
    private double locations[][];
    private double new_location [];
    public static  TextView longitude_text;
    TextView latitude_text;
    TextView altitude_text;
    TextView thank_you_text;
    TextView value_count_text;
    ListView airListView;
    GPSTracker gps;
    DBWriter dbWriter;
    Button gps_button;
    Button stop_button;
    Handler m_handler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        dbWriter=new DBWriter(this);
        longitude_text = (TextView) findViewById(R.id.longitude_text);
        latitude_text = (TextView) findViewById(R.id.latitude_text);
        altitude_text = (TextView) findViewById(R.id.altitude_text);
        thank_you_text = (TextView) findViewById(R.id.thank_you_text);
        value_count_text = (TextView) findViewById(R.id.value_count_text);
        airListView=(ListView) findViewById(R.id.listview_all_air);
        gps_button = (Button) findViewById(R.id.gps_button);
        stop_button = (Button) findViewById(R.id.stop_button);
        gps = new GPSTracker(this);
        m_handler = new Handler();
        locations = new double [3][max_value_count];


        double[] fakeData = {1, 2, 3, 4, 5, 6};

        dbWriter = new DBWriter(this);
        dbWriter.addItem("Fake Data", fakeData);
        gps.showSettingsAlert();
        stop_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Stop GPS Handler
                m_handler.removeCallbacks(mStatusChecker);
                try {
                    showValueCount();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.v("Info", "Stopped.");
            }
        });

        gps_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Run GPS Handler
                mStatusChecker.run();
            }
        });


        // At some point if you need call service method with parameter:

        myAirAdapter=new AirAdapter(this.getApplicationContext(),null,0);
        Intent intent=new Intent(getApplicationContext(), SQLiteDatabaseFunctions.class);
        startService(intent);
        airListView.setAdapter(myAirAdapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {

            Log.v("Info", "Capturing GPS Reading");
            if (value_count <= max_value_count) {

                new_location = gps.getGps();

                locations[0][value_count] = new_location[0];
                locations[1][value_count] = new_location[1];
                locations[2][value_count] = new_location[2];

                longitude_text.setText("Longitude: " + String.valueOf(new_location[0]));
                latitude_text.setText("Latitude: " + String.valueOf(new_location[1]));
                altitude_text.setText("Altitude: " + String.valueOf(new_location[2]));

                value_count = gps.getValueCount();

                m_handler.postDelayed(mStatusChecker, m_interval);
            }

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showValueCount() throws IOException {
        value_count_text.setText("Value Count: " + value_count);
        thank_you_text.setText("Thank you for using. Have a nice exercise!");
    }


    public static final int HOBBIE_LOADER=0;
    private AirAdapter myAirAdapter;

    public final String[] COLUMNS={
            AirContract.AirEntry.TABLE_NAME+"."+ AirContract.AirEntry._ID,
            AirContract.AirEntry.COLUMN_SMOG_VALUE,
            AirContract.AirEntry.COLUMN_AIR_QUALITY,
            AirContract.AirEntry.COLUMN_TIME,
            AirContract.AirEntry.COLUMN_LONG,
            AirContract.AirEntry.COLUMN_LAT,
            AirContract.AirEntry.COLUMN_ALT
    } ;

    public static final int COLUMN_ID=0;
    public static final int COLUMN_SMOG_VALUE=1;
    public static final int COLUMN_AIR_QUALITY=2;
    public static final int COLUMN_TIME=3;
    public static final int COLUMN_LONG=4;
    public static final int COLUMN_LAT=5;
    public static final int COLUMN_ALT=6;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getApplicationContext(),
                AirContract.AirEntry.CONTENT_URI,
                COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        myAirAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        myAirAdapter.swapCursor(null);
    }


}
