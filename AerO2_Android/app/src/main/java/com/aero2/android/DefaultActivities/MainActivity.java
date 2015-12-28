package com.aero2.android.DefaultActivities;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aero2.android.DefaultClasses.Integrator;

import com.aero2.android.R;

public class MainActivity extends AppCompatActivity {

    //Main Activity Variables
    private int m_interval = 1500;              // Time between each Integrator call

    //UI Elements
    private TextView smog_text;
    private TextView location_text;
    private TextView time_text;
    private TextView count_text;
    private TextView thank_you_text;
    private ImageView location_image;
    private ImageView time_image;
    private Toolbar toolbar;
    private Button gps_button;
    private Button stop_button;
    private Handler m_handler;

    //Global Objects
    private Integrator integrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instantiate UI Objects
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        smog_text = (TextView) findViewById(R.id.smog_text);
        location_text = (TextView) findViewById(R.id.location_text);
        time_text = (TextView) findViewById(R.id.time_text);
        count_text = (TextView) findViewById(R.id.count_text);
        thank_you_text = (TextView) findViewById(R.id.thank_you_text);
        location_image = (ImageView) findViewById(R.id.location_image);
        time_image = (ImageView) findViewById(R.id.time_image);
        gps_button = (Button) findViewById(R.id.gps_button);
        stop_button = (Button) findViewById(R.id.stop_button);

        //Instantiate Objects
        m_handler = new Handler();
        integrator = new Integrator(this);

        //Set visibility of ImageViews
        location_image.setVisibility(View.INVISIBLE);
        time_image.setVisibility(View.INVISIBLE);

        //Start saving data in Azure
        integrator.saveAzure();

        //Ask user to adjust settings
        setSupportActionBar(toolbar);
        this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);                         //Ask user for Manifest permission


        //Start Integrator Handler
        gps_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Set defaults
                smog_text.setText("0");
                location_text.setText("--,--");
                time_text.setText("--:--:--");
                count_text.setText("0");

                location_image.setVisibility(View.VISIBLE);
                time_image.setVisibility(View.VISIBLE);
                //Run GPS Handler
                getIntegrator.run();
            }
        });


        //Stop Integrator Handler & show count
        stop_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Stop Integrator Handler
                m_handler.removeCallbacks(getIntegrator);
                thank_you_text.setText(getString(R.string.final_text));
                integrator.saveSQL();

                Log.v("MainActivity", "Stopped Integrator.");
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

            integrator.init(smog_text, location_text, time_text, count_text);

            //Call again after delay of m_interval
            m_handler.postDelayed(getIntegrator,m_interval);

        }
    };

}
