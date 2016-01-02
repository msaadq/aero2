package com.aero2.android.DefaultClasses;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.aero2.android.DefaultClasses.Azure.AzureHandler;
import com.aero2.android.DefaultClasses.Hardware.BTService;
import com.aero2.android.DefaultClasses.Hardware.STMCommunicator;
import com.aero2.android.DefaultClasses.SQLite.SamplesSQLite;
import com.aero2.android.DefaultClasses.SQLite.SQLiteAsyncTask;

/**
 *
 * Instantiates STMCommunicator's and GPSTracker's objects and
 * integrates them.
 *
 * USE CASE:
 *        - First initialize by passing on the activity.
 *        - Call init() method which in turns calls private method
 *        integrateSmog()
 *        - saveSQL() method saves data in local SQL Storage.
 *        - saveAzure() method saves data in Azure.
 *
 * Created by Usman on 11/17/2015.
 */

public class Integrator {

    //Local class variables
    private final int maxValueCount = 10000;
    private final int maxCounter = 8;
    private final int N = 6;                    // Size of integrator array
    public static int value_count = 0;
    private int counter;
    private Double integrators[][];             // 2-D array holding all records
    private Double new_integrator [];
    private Double start_lat, start_lon;
    //private int[] corner;

    //Global variables
    private Activity activity;
    STMCommunicator sensor;
    GPSTracker gps;
    private SQLiteAsyncTask sqLiteAsyncTask;
    public SamplesSQLite samplesSqLite;
    private AzureHandler azureHandler;

    /**
     * Initializes the constructor by instantiating
     * GPSTracker & STMCommunicator Objects
     * arg: The Current Activity
     * exception: IOException
     * return: No return value.
     */

    public Integrator(Activity activity) {

        this.activity = activity;

        integrators = new Double [maxValueCount][N];
        //corner = new int[4];
        counter = 0;


        try {
            gps = new GPSTracker(activity);
            sensor = new STMCommunicator(activity);
            samplesSqLite = new SamplesSQLite(activity);
            //samplesSqLite.emptySQL();

        } catch (IOException e) {
            Log.d("Integrator ", "Initialization failed.");
        }
    }

    public Double[] getLocation(){
        return gps.getGps();
    }

    /**
     * Integrates Smog with GPS and time values.
     * arg: None
     * exception: IOException
     * return: Double array containing date&time,
     * longitude, latitude, altitude, smog and normalized
     * (in order)
     */

    private Double[] integrateSmog() {

        if (!BTService.getBluetoothAdapter()){
            return null;
        }

        Double[] integrated = new Double[N];
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyMMdd.HHmmss");   //dd/MM/yyyy
        Date date = new Date();
        String dateTime = sdfDate.format(date);

        try {

            if(value_count == 0) {
                sensor.authenticate("username", "password");
                sensor.enableSensors();
            }

            String smog = sensor.getSmogValue();
            Double[] newLocation = gps.getGps();

            integrated[0] = Double.valueOf(dateTime);
            integrated[1] = newLocation[0];
            integrated[2] = newLocation[1];
            integrated[3] = newLocation[2];
            integrated[4] = Double.valueOf(smog);
            integrated[5] = 0.0;                    //Normalized is set to default '0'

        } catch (IOException ie) {
            ie.printStackTrace();
        }

        return integrated;
    }

    public boolean sensorEnable(){
        try {
            return sensor.enableSensors();
        }
        catch (IOException e){
            Log.w("Integrator: ", "Sensor Authentication Failed");
            return false;
        }
    }

    public void sensorDisable(){
        try {
            sensor.disableSensors();
        }
        catch (IOException e){
            Log.w("Integrator: ","Sensor Authentication Failed");
        }
    }


    /**
     * Calls integrateSmog() method and handles exception.
     * AVAILABLE FOR OUTSIDE APIS.
     * arg: None
     * exception: IOException
     * return: String
     */

    public void init (TextView smog_text, TextView location_text, TextView time_text,
                      TextView count_text, FloatingActionButton bluetoothStatus,
                      FloatingActionButton locationStatus, TextView sensorStatusText,
                      Switch sensorSwitch){

        Boolean valid = true;

        if (GPSTracker.getGPSStatus() && BTService.getDeviceConnected()
                && BTService.getBluetoothAdapter()) {

            bluetoothStatus.setVisibility(View.INVISIBLE);
            locationStatus.setVisibility(View.INVISIBLE);
            sensorStatusText.setText("ON");
            sensorSwitch.setChecked(false);

            new_integrator = integrateSmog();

            //Parse information
            for (int i = 0; i < N; i++) {
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
            if (integrators[value_count][4] == 0 || integrators[value_count][4] > 1024 ){
                Log.v("MainActivity", "Smog = 0");
                valid = false;
            }

            if (valid) {

                if (value_count == 0){
                    start_lat = integrators[0][1];
                    Log.e("Start lat: ",String.valueOf(start_lat));

                    start_lon = integrators[0][2];
                    Log.e("Start lon: ",String.valueOf(start_lon));
                }

                //int curr_lat = (int) (Float.parseFloat(integrators[0][1]) * 100);
                //int curr_lon = (int) (Float.parseFloat(integrators[0][2]) * 100);

                SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");   //dd/MM/yyyy
                Date date = new Date();
                String time = sdfDate.format(date);

                value_count++;
                Log.v("Value Count", String.valueOf(value_count));

                Log.v("Integrator get value", "Success");

                time_text.setText(time);
                location_text.setText(String.format("%.2f", integrators[value_count - 1][1])
                        + ", " + String.format("%.2f", integrators[value_count - 1][2]));

                smog_text.setText(String.valueOf((integrators[value_count - 1][4]).longValue()));
                count_text.setText("["+String.valueOf(value_count)+"]");

            }

            else{
                Log.e("Integrator","Location values are null");
            }
        }

        else if(!GPSTracker.getGPSStatus()){
            Log.e("Integrator","GPS is not connected");
            bluetoothStatus.setVisibility(View.INVISIBLE);
            locationStatus.setVisibility(View.VISIBLE);
        }

        else if(!BTService.getBluetoothAdapter()){
            bluetoothStatus.setVisibility(View.VISIBLE);
            locationStatus.setVisibility(View.INVISIBLE);
            Log.e("BTAdapter status:", "Disconnected");
            BTService.setDeviceConnected(false);
            Log.e("Integrator","Bluetooth is not connected");
        }

        else if (!BTService.getDeviceConnected()){

            bluetoothStatus.setVisibility(View.INVISIBLE);
            locationStatus.setVisibility(View.INVISIBLE);
            if (counter > maxCounter){

                try {
                    sensor = new STMCommunicator(activity);
                    counter = 0;
                }catch (IOException e){
                    Log.d("STMCommunicator ", "Failed to reinitialize BT");
                }

                //BT has reinitialized; will connect again
                Log.w("Integrator","BT is trying to connect");

            }
            else{
                Log.v("Counter status: ","Incrementing counter");
                counter ++;

                //BT is in process of connecting
                Log.w("Integrator", "BT is trying to connect");
            }
        }

    }

    /**
     * Saves data to local storage.
     * arg: None
     * exception: None
     * return: No return value.
     */

    public void saveSQL (){

        //Save just 1 row in SQL
        Double[][] temp = new Double[1][N];

        /*

        //Save starting time & location
        temp[0][0] = integrators[0][0];
        temp[0][1] = integrators[0][1];
        temp[0][2] = integrators[0][2];
        temp[0][3] = integrators[0][3];
        temp[0][5] = integrators[0][5];

        //Average the smog value
        Double sum = 0.0;
        for (int i=0;i<value_count;i++){
            sum += integrators[i][4];
        }

        temp[0][4] = sum/value_count;
        Log.v("Smog Averaged",String.valueOf(temp[0][4]));

        //Set value count to 1
        value_count = 1;

        */
        //Call SamplesSQLite
        sqLiteAsyncTask = new SQLiteAsyncTask(activity, samplesSqLite);
        sqLiteAsyncTask.execute(integrators);

        //Reinitialize
        integrators = new Double [maxValueCount][N];;
    }

    /**
     * Saves data in Azure.
     * arg: None
     * exception: None
     * return: No return value.
     */

    public void saveAzure(){

        azureHandler = new AzureHandler(activity);
        azureHandler.postSamples(samplesSqLite);
    }

    /**
     * Calculates reference corner points.
     * arg: Starting latitude & longitude (int)
     * exception: None
     * return: Int array of reference values:
     * Lower Latitude, Lower Longitude, Upper Latitude,
     * Upper longitude.
     */

    private void computeReference(){


    }
}