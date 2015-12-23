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
import com.aero2.android.DefaultClasses.SQLiteAPI;
import com.aero2.android.DefaultClasses.SQLiteAsyncTask;
import com.aero2.android.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    //Main Activity Variables
    private int m_interval = 1500;              // Time between each GPS recording
    public static int value_count = 0;
    private final int max_value_count = 1000;
    private final int N = 6;                    // Size of integrator array
    private String integrators[][];             // 2-D array holding all records
    private String new_integrator [];
    private Boolean sessionStart;

    //UI Elements
    private TextView update_message_text;
    private TextView longitude_text;
    private TextView latitude_text;
    private TextView altitude_text;
    private TextView smog_text;
    private TextView time_text;
    private TextView thank_you_text;
    private TextView value_count_text;
    private Toolbar toolbar;

    //Global Objects
    private Date date;
    public GPSTracker gps;
    private Integrator integrator;
    private DBWriter dbWriter;
    private DBAsyncTask dbAsyncTask;
    private SQLiteAsyncTask sqLiteAsyncTask;
    private SQLiteAPI sqLiteAPI;
    private Button gps_button;
    private Button stop_button;
    private Handler m_handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instantiate UI Objects
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        update_message_text= (TextView) findViewById(R.id.update_message_text);
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
        sqLiteAPI = new SQLiteAPI(this);
        integrator = new Integrator(this);
        integrators = new String [max_value_count][N];
        sessionStart = true;

        //Start save data in Azure
        saveAzure();

        //Ask user to adjust settings
        setSupportActionBar(toolbar);
        this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);                         //Ask user for Manifest permission


        //Stop Integrator Handler & show count
        stop_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Stop Integrator Handler
                m_handler.removeCallbacks(getIntegrator);

                try {
                    showValueCount();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.v("MainActivity", "Stopped Integrator.");
            }
        });

        //Start Integrator Handler
        gps_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                update_message_text.setText(R.string.warm_up_message);

                //If the activity is not started for first time
                if(!sessionStart){
                    Log.v("MainActivity","Reinitializing objects");

                    //Clear update & thank you texts if starting again.
                    update_message_text.setText("");
                    thank_you_text.setText("");

                    //Reinitialize integrators
                    integrators = new String [max_value_count][N];
                    value_count = 0;

                }

                //Session has already started.
                sessionStart = false;

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

    /**
     * Runnable Handler that calls Integrator class and
     * updates UI.
     * arg: None
     * exception: None
     * return: No return value.
     */

    Runnable getIntegrator = new Runnable() {
        @Override
        public void run() {

            boolean valid = true;           //reference variable indicating if values are correct

            //Check if Bluetooth is connected & GPS is turned 'on'
            if (GPSTracker.getGPSStatus() && BTService.getDeviceConnected()) {

                //Check if maximum limit is not exceeded
                if (value_count <= max_value_count) {

                    //Get smog and GPS values
                    new_integrator = integrator.integrateSmog();

                    //Parse information
                    for (int i = 0; i < 6; i++) {
                        integrators[value_count][i] = new_integrator[i];
                    }

                    //Skip if the location values contain null
                    for (int i = 1; i<4; i++) {
                        if(integrators[value_count][i] == null){
                            Log.v("MainActivity","Locations are null");
                            valid = false;
                        }
                    }

                    //Skips if smog sensor's value is 0
                    if (integrators[value_count][4].equals("0")){
                        Log.v("MainActivity","Smog = 0");
                        valid = false;
                    }

                    if (valid) {

                        updateUI(new_integrator);
                        value_count++;
                        Log.v("Value Count", String.valueOf(value_count));
                        m_handler.postDelayed(getIntegrator, m_interval);

                    }

                    else{

                        m_handler.postDelayed(getIntegrator, 500);
                        update_message_text.setText(R.string.warm_up_message);

                    }
                }
            }
            else{
                update_message_text.setText(R.string.bt_gps_error_message);
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

    /**
     * Shows value count & saves data to local storage.
     * arg: None
     * exception: IO Exception
     * return: No return value.
     */

    public void showValueCount() throws IOException {

        value_count_text.setText("Value Count: " + value_count);
        sqLiteAsyncTask = new SQLiteAsyncTask(MainActivity.this,sqLiteAPI);
        sqLiteAsyncTask.execute(integrators);

        thank_you_text.setText(getString(R.string.final_text));

    }

    /**
     * Saves data in Azure.
     * arg: None
     * exception: None
     * return: No return value.
     */

    public void saveAzure(){

        dbAsyncTask = new DBAsyncTask(this,dbWriter,sqLiteAPI);
        dbAsyncTask.execute(integrators);

    }

    /**
     * Updates UI elements.
     * arg: None
     * exception: None
     * return: No return value.
     */

    public void updateUI(String[] new_integrator){

        if (value_count ==0){
            update_message_text.setText("");
        }

        //Add Date Information
        SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");
        date = new Date();

        //Update UI Elements
        time_text.setText("Time & Date: " + sdfDate.format(date));
        longitude_text.setText("Longitude: " + new_integrator[1]);
        latitude_text.setText("Latitude: " + new_integrator[2]);
        altitude_text.setText("Altitude: " + new_integrator[3]);
        smog_text.setText("Smog: " + new_integrator[4]);

    }

}
