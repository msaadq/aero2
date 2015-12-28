package com.aero2.android.DefaultActivities;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aero2.android.DefaultClasses.Integrator;

import com.aero2.android.R;

public class MainActivity extends AppCompatActivity {

    //Main Activity Variables
    private int m_interval = 1500;              // Time between each Integrator call
    private String smog;

    //UI Elements
    private TextView update_message_text;
    private TextView smog_text;
    private TextView thank_you_text;
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
        update_message_text= (TextView) findViewById(R.id.update_message_text);
        smog_text = (TextView) findViewById(R.id.smog_text);
        thank_you_text = (TextView) findViewById(R.id.thank_you_text);
        gps_button = (Button) findViewById(R.id.gps_button);
        stop_button = (Button) findViewById(R.id.stop_button);

        //Instantiate Objects
        m_handler = new Handler();
        integrator = new Integrator(this);

        //Start saving data in Azure
        integrator.saveAzure();

        //Ask user to adjust settings
        setSupportActionBar(toolbar);
        this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);                         //Ask user for Manifest permission


        //Start Integrator Handler
        gps_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Show default value of smog as 0
                smog_text.setText("0");
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
            smog = integrator.init();

            if (smog == "0"){
                update_message_text.setText(R.string.update_message_0);
            }
            else if (smog == "-1"){
                update_message_text.setText(R.string.update_message_1);
            }
            else if (smog == "-2"){
                update_message_text.setText(R.string.update_message_2);
            }
            else if (smog == "-3"){
                update_message_text.setText(R.string.update_message_3);
            }
            else if (smog == "-4"){
                update_message_text.setText(R.string.update_message_4);
            }
            else if (smog == "-5"){
                update_message_text.setText(R.string.update_message_5);
            }
            else{
                update_message_text.setText(R.string.update_message_6);
                smog_text.setText(smog);
            }

            //Call again after delay of m_interval
            m_handler.postDelayed(getIntegrator,m_interval);

        }
    };

}
