package com.aero2.android.DefaultClasses;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.aero2.android.DefaultClasses.Azure.AzureHandler;
import com.aero2.android.DefaultClasses.Azure.DBWriter;
import com.aero2.android.DefaultClasses.DataTables.SampleDataTable;
import com.aero2.android.DefaultClasses.Hardware.BTService;
import com.aero2.android.DefaultClasses.Hardware.STMCommunicator;
import com.aero2.android.DefaultClasses.SQLite.SQLiteAPI;
import com.aero2.android.DefaultClasses.SQLite.SQLiteAsyncTask;
import com.aero2.android.R;

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
    public SQLiteAPI sqLiteAPI;
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
            sqLiteAPI = new SQLiteAPI(activity);


        } catch (IOException e) {
            Log.d("Integrator ", "Initialization failed.");
        }
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

            sensor.authenticate("username", "password");
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


    /**
     * Calls integrateSmog() method and handles exception.
     * AVAILABLE FOR OUTSIDE APIS.
     * arg: None
     * exception: IOException
     * return: String
     */

    public void init (TextView smog_text, TextView location_text, TextView time_text,
                      TextView count_text){

        Boolean valid = true;

        if (GPSTracker.getGPSStatus() && BTService.getDeviceConnected()
                && BTService.getBluetoothAdapter()) {
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

                Toast toast = Toast.makeText(activity, R.string.update_message_6, Toast.LENGTH_SHORT);
                toast.show();

                time_text.setText(time);
                location_text.setText(String.format("%.2f", integrators[value_count - 1][1])
                        + ", " + String.format("%.2f", integrators[value_count - 1][2]));

                smog_text.setText(String.valueOf((integrators[value_count - 1][4]).longValue()));
                count_text.setText("["+String.valueOf(value_count)+"]");

            }

            else{
                Toast toast = Toast.makeText(activity, R.string.update_message_0,
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        else if(!GPSTracker.getGPSStatus()){
            Toast toast = Toast.makeText(activity, R.string.update_message_1,
                    Toast.LENGTH_SHORT);
            toast.show();
        }

        else if(!BTService.getBluetoothAdapter()){
            Log.v("BTAdapter status:","Disconnected");
            BTService.setDeviceConnected(false);
            Toast toast = Toast.makeText(activity, R.string.update_message_2,
                    Toast.LENGTH_SHORT);
            toast.show();
        }

        else if (!BTService.getDeviceConnected()){

            if (counter > maxCounter){

                try {
                    sensor = new STMCommunicator(activity);
                    counter = 0;
                }catch (IOException e){
                    Log.d("STMCommunicator ", "Failed to reinitialize BT");
                }

                //BT has reinitialized; will connect again
                Toast toast = Toast.makeText(activity, R.string.update_message_3,
                        Toast.LENGTH_SHORT);
                toast.show();

            }
            else{
                Log.v("Counter status: ","Incrementing counter");
                counter ++;

                //BT is in process of connecting
                Toast toast = Toast.makeText(activity, R.string.update_message_4,
                        Toast.LENGTH_SHORT);
                toast.show();
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

        //Call SQLiteAPI
        sqLiteAsyncTask = new SQLiteAsyncTask(activity,sqLiteAPI);
        sqLiteAsyncTask.execute(temp);

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
        azureHandler.postSamples(sqLiteAPI);
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