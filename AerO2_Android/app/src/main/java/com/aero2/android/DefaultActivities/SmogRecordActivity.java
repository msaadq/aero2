package com.aero2.android.DefaultActivities;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.aero2.android.DefaultClasses.Azure.AzureHandler;
import com.aero2.android.DefaultClasses.Hardware.BTService;
import com.aero2.android.DefaultClasses.Integrator;

import com.aero2.android.R;

public class SmogRecordActivity extends AppCompatActivity {

    //Main Activity Variables
    private int m_interval = 1500;              // Time between each Integrator call
    private static boolean sensorResponse;

    //UI Elements
    private TextView smog_text;
    private TextView location_text;
    private TextView time_text;
    private TextView count_text;
    private TextView sensorStatusText;
    private ImageView location_image;
    private ImageView time_image;
    private Toolbar toolbar;
    private Handler m_handler;
    private FloatingActionButton startSensor;
    private FloatingActionButton stopSensor;
    private FloatingActionButton bluetoothStatus;
    private FloatingActionButton locationStatus;
    private Switch sensorSwitch;


    //Global Objects
    private Integrator integrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instantiate UI Objects
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        startSensor = (FloatingActionButton) findViewById(R.id.start_sensor);
        stopSensor = (FloatingActionButton) findViewById(R.id.stop_sensor);
        bluetoothStatus = (FloatingActionButton) findViewById(R.id.bluetooth_status);
        locationStatus = (FloatingActionButton) findViewById(R.id.location_status);
        sensorStatusText = (TextView) findViewById(R.id.sensor_status_text);
        smog_text = (TextView) findViewById(R.id.smog_text);
        location_text = (TextView) findViewById(R.id.location_text);
        time_text = (TextView) findViewById(R.id.time_text);
        count_text = (TextView) findViewById(R.id.count_text);
        location_image = (ImageView) findViewById(R.id.location_image);
        time_image = (ImageView) findViewById(R.id.time_image);
        sensorSwitch = (Switch) findViewById(R.id.switchButton);

        //Instantiate Objects
        m_handler = new Handler();
        integrator = new Integrator(this);

        locationStatus.setVisibility(View.INVISIBLE);
        bluetoothStatus.setVisibility(View.INVISIBLE);


        //Set visibility of ImageViews
        location_image.setVisibility(View.INVISIBLE);
        time_image.setVisibility(View.INVISIBLE);
        stopSensor.setVisibility(View.INVISIBLE);

        //Start saving data in Azure
        integrator.saveAzure();

        //Ask user to adjust settings
        setSupportActionBar(toolbar);
        this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);                         //Ask user for Manifest permission


        //Start integrator handler
        startSensor.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                stopSensor.setVisibility(View.VISIBLE);
                startSensor.setVisibility(View.INVISIBLE);

                //Set defaults
                smog_text.setText("0");
                location_text.setText("--,--");
                time_text.setText("--:--:--");
                count_text.setText("[0]");

                location_image.setVisibility(View.VISIBLE);
                time_image.setVisibility(View.VISIBLE);
                //Run GPS Handler
                getIntegrator.run();
            }
        });

        //Stop integrator handler and show count
        stopSensor.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopSensor.setVisibility(View.INVISIBLE);
                startSensor.setVisibility(View.VISIBLE);

                //Stop Integrator Handler
                m_handler.removeCallbacks(getIntegrator);
                //thank_you_text.setText(getString(R.string.final_text));
                integrator.saveSQL();

                Log.v("MainActivity", "Stopped Integrator.");
            }

        });

        sensorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    if (BTService.getDeviceConnected()) {
                        sensorResponse = integrator.sensorEnable();
                        sensorStatusText.setText("ON");
                    } else {
                        sensorResponse = false;
                        sensorStatusText.setText("OFF");

                        Log.v("sensorSwitch listener", "Device isn't connected yet.");
                    }

                    sensorSwitch.setChecked(sensorResponse);


                } else {
                    integrator.sensorDisable();
                    sensorStatusText.setText("OFF");

                }
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

            integrator.init(smog_text, location_text, time_text, count_text,
                    bluetoothStatus,locationStatus,sensorStatusText,sensorSwitch);

            //Call again after delay of m_interval
            m_handler.postDelayed(getIntegrator,m_interval);

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_test, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);


    }

}
