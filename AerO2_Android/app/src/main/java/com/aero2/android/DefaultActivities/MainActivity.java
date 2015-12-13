package com.aero2.android.DefaultActivities;


import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.aero2.android.DefaultClasses.DBWriter;
import com.aero2.android.DefaultClasses.GPSTracker;
import com.aero2.android.DefaultClasses.Integrator;
import com.aero2.android.DefaultClasses.STMAsyncTask;
import com.aero2.android.DefaultClasses.STMCommunicator;
import com.aero2.android.R;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // Time between each GPS recording
    private int m_interval = 1000;
    private int value_count = 0;
    private final int max_value_count = 1000;
    private double locations[][];
    private double new_location [];
    TextView longitude_text;
    TextView latitude_text;
    TextView altitude_text;
    TextView thank_you_text;
    TextView value_count_text;
    GPSTracker gps;
    Integrator integrator;
    STMAsyncTask stmTask;
    Button gps_button;
    Button stop_button;
    Handler m_handler;


    /// Debug Code

    // STMCommunicator stmCommunicator;
    //DBWriter dbWriter;

    ///

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        longitude_text = (TextView) findViewById(R.id.longitude_text);
        latitude_text = (TextView) findViewById(R.id.latitude_text);
        altitude_text = (TextView) findViewById(R.id.altitude_text);
        thank_you_text = (TextView) findViewById(R.id.thank_you_text);
        value_count_text = (TextView) findViewById(R.id.value_count_text);
        gps_button = (Button) findViewById(R.id.gps_button);
        stop_button = (Button) findViewById(R.id.stop_button);
        gps = new GPSTracker(this);
        m_handler = new Handler();
        locations = new double [3][max_value_count];

        setSupportActionBar(toolbar);
        gps.showSettingsAlert();            //Ask user to turn on location
        this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);                         //Ask user for Manifest permission

        stmTask = new STMAsyncTask(this);
        stmTask.execute();

        integrator = new Integrator(this);
        ///


        /*
        try {
            stmCommunicator = new STMCommunicator(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        /// Debug Code

        //double[] fakeData = {1, 2, 3, 4, 5, 6};

        //dbWriter = new DBWriter(this);
        //dbWriter.addItem("Fake Data", fakeData);

        ///

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {

            Log.v("Status", "Capturing GPS Reading");
            if (value_count <= max_value_count) {

                new_location = gps.getGps();
                Log.v("Status","Inside if condition");
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

}
