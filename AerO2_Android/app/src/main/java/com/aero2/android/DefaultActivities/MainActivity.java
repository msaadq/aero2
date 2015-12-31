package com.aero2.android.DefaultActivities;


import android.Manifest;
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

import com.aero2.android.DefaultClasses.BTService;
import com.aero2.android.DefaultClasses.DBAsyncTask;
import com.aero2.android.DefaultClasses.DBWriter;
import com.aero2.android.DefaultClasses.GPSTracker;
import com.aero2.android.DefaultClasses.Integrator;
import com.aero2.android.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // Time between each GPS recording
    private int m_interval = 1000;
    public static int value_count = 0;
    private final int max_value_count = 1000;
    private double integrators[][];
    private double new_integrator [];
    public static TextView longitude_text;
    public static TextView latitude_text;
    public static TextView altitude_text;
    public static TextView smog_text;
    TextView time_text;
    TextView thank_you_text;
    TextView value_count_text;
    Date date;
    GPSTracker gps;
    Integrator integrator;
    DBWriter dbWriter;
    DBAsyncTask dbAsyncTask;
    Button gps_button;
    Button stop_button;
    Handler m_handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instantiate UI Objects
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        longitude_text = (TextView) findViewById(R.id.longitude_text);
        latitude_text = (TextView) findViewById(R.id.latitude_text);
        altitude_text = (TextView) findViewById(R.id.altitude_text);
        smog_text = (TextView) findViewById(R.id.smog_text);
        time_text = (TextView) findViewById(R.id.time_text);
        thank_you_text = (TextView) findViewById(R.id.thank_you_text);
        value_count_text = (TextView) findViewById(R.id.value_count_text);
        gps_button = (Button) findViewById(R.id.gps_button);
        stop_button = (Button) findViewById(R.id.stop_button);

        //Instantiate Objects
        gps = new GPSTracker(this);
        m_handler = new Handler();
        dbWriter = new DBWriter(this);
        integrator = new Integrator(this);
        integrators = new double [max_value_count][6];

        //Ask user to adjust settings
        setSupportActionBar(toolbar);
        gps.showSettingsAlert();            //Ask user to turn on location
        this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);                         //Ask user for Manifest permission

        stop_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Stop GPS Handler
                m_handler.removeCallbacks(getIntegrator);
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
                Log.v("gps_button","Inside setonClickListener()");
                //Run GPS Handler
                getIntegrator.run();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Relevant Action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    Runnable getIntegrator = new Runnable() {
        @Override
        public void run() {
            if (BTService.getDeviceConnected() == true) {
                Log.v("Status", "Capturing GPS Reading");
                if (value_count <= max_value_count) {

                    new_integrator = integrator.integrateSmog();

                    for (int i = 0; i < 6; i++) {
                        integrators[value_count][i] = new_integrator[i];
                    }

                    SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");
                    date = new Date();

                    time_text.setText("Time & Date: " + sdfDate.format(date));
                    longitude_text.setText("Longitude: " + new_integrator[1]);
                    latitude_text.setText("Latitude: " + new_integrator[2]);
                    altitude_text.setText("Altitude: " + new_integrator[3]);
                    smog_text.setText("Smog: " + new_integrator[4]);

                    value_count++;
                    Log.v("Value Count", String.valueOf(value_count));
                    m_handler.postDelayed(getIntegrator, m_interval);
                }
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
        Log.v("MainActivity","Calling constructor");

        dbAsyncTask = new DBAsyncTask(this,dbWriter);
        Log.v("MainActivity","Calling execute");

        dbAsyncTask.execute(integrators);
        thank_you_text.setText("Thank you for contributing! Your values have been recorded." +
                " Have a nice exercise!");
    }

}
