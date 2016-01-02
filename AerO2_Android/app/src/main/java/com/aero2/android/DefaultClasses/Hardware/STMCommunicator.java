package com.aero2.android.DefaultClasses.Hardware;

import android.app.Activity;
import android.util.Log;

import java.io.IOException;

/**
 * This communicates with the BT module using the BTService.java class.
 * It introduces the Authentication and Communication protocols to be used with STM via BT Module.
 * The default format for a Communication command will be O[3-dig Command]:[Arguments] eg. OPAS:password
 * O represents outgoing command and I represents incoming command
 *
 * Usage Guide:
 * STMCommunicator stmCommunicator = new STMCommunicator(activity);
 * int[] sensorValues = stmCommunicator.getSensorValues();
 * stmCommunicator.disableSensors();
 *
 * Created by Saad on 11/15/2015.
 */

public class STMCommunicator {
    public static final String BT_DEVICE_NAME = "HC-05"; // Default device name
    public static final int DEFAULT_I_COMMAND_SIZE = 20; // eg. "IPAS:1"
    public static String batteryLevel;

    public int nSensors = 0; // No. of available sensors

    BTService btService; // Bluetooth Service class allowing BT communication

    // All the available Out commands
    public final String O_COM_AUT = "OAUT"; // Authenticate
    public final String O_COM_USR = "OUSR"; // Set Username
    public final String O_COM_PAS = "OPAS"; // Set Password
    public final String O_COM_NSG = "ONSG"; // Enable / Disable Smog Sensor
    public final String O_COM_NAQ = "ONAQ"; // Enable / Disable Air Quality Sensor
    public final String O_COM_SSG = "OSSG"; // Request Smog Data
    public final String O_COM_SAQ = "OSAQ"; // Request Air Quality Data

    // All the available In commands
    public final String I_COM_AUT = "IAUT"; // Authentication Status
    public final String I_COM_USR = "IUSR"; // Username Status
    public final String I_COM_PAS = "IPAS"; // Password Status
    public final String I_COM_NSG = "INSG"; // Smog Sensor Status
    public final String I_COM_NAQ = "INAQ"; // Air Quality Sensor Status
    public final String I_COM_SSG = "ISSG"; // Received Smog Data
    public final String I_COM_SAQ = "ISAQ"; // Received Air Quality Data

    // Authentication Strings
    public static final String DEF_USERNAME = "username";
    public static final String DEF_PASSWORD = "password";

    // Booleans representing program status
    public boolean btAvailable = false;
    public boolean authenticationStatus = false;
    public boolean userCorrect = false;
    public boolean passwordCorrect = false;
    public boolean smogAvailable = false;
    public boolean airQualityAvailable = false;
    public boolean isDeviceConnected = false;


    /**
     * Default Constructor
     * Instantiates the BTService object
     * arg: Current Activity
     * exception: IOException
     * return: No return value
     */

    public STMCommunicator(Activity activity) throws IOException {
        this.btService = new BTService(activity, BT_DEVICE_NAME, this);
    }

    /**
     * Sends the O commands to the BT device according to the format defined above
     * arg: Command Prefix (4-digit), Command Arguments
     * exception: IOException
     * return: No return value
     */

    private void sendCommand(String commandPrefix, String commandArguments) throws IOException {
        String message = commandPrefix + ":" + commandArguments;
        this.btService.sendMessage(message);
    }

    /**
     * Receives the I commands from the BT device according to the format defined above
     * arg: None
     * exception: IOException
     * return: The Received String Command
     */

    private String receiveCommand() throws IOException {
        return this.btService.readMessage(DEFAULT_I_COMMAND_SIZE);
    }

    /**
     * Authenticates the phone with the MCU via BT and Activates the sensors
     * arg: Username, Password
     * exception: IOException
     * return: No return value.
     */

    public void authenticate(String username, String password) throws IOException {
        if (BTService.getBluetoothAdapter() && BTService.getDeviceConnected()) {

            // Step 1 : Send the AUT: Instruction for initial identification and wait for approval
            sendCommand(O_COM_AUT, "1\n");

            if (receiveCommand().equals(I_COM_AUT + ":1")) {
                btAvailable = true;
                Log.v("STMCommunicator", "btAvailable is set to true, authenticate()");

            }


            // Step 2 : Send the Username and wait for approval
            sendCommand(O_COM_USR, username + "\n");

            if (receiveCommand().equals(I_COM_USR + ":1")) {
                userCorrect = true;
                Log.v("STMCommunicator", "username is set to true, authenticate()");
            }

            // Step 3 : Send the Password and wait for approval
            sendCommand(O_COM_PAS, password + "\n");

            if (receiveCommand().equals(I_COM_PAS + ":1")) {
                passwordCorrect = true;
                Log.v("STMCommunicator", "password is set to true, authenticate()");
            }

            authenticationStatus = btAvailable && userCorrect && passwordCorrect;
            Log.v("Authenticated? ", String.valueOf(authenticationStatus));

        }
    }


    /**
     * Activates all the available sensors
     * arg: None
     * exception: IOException
     * return: No return value.
     */

    public boolean enableSensors() throws IOException {
        sendCommand(O_COM_NSG, "1\n");

        if(receiveCommand().equals(I_COM_NSG + ":1")) {
            smogAvailable = true;
            nSensors++;
            Log.v("Smog sensor: ", "Authenticated");
            return true;
        }

        else{
            return false;
        }

    }

    /**
     * Deactivates all the available sensors
     * arg: None
     * exception: IOException
     * return: No return value.
     */

    public void disableSensors() throws IOException {

        if(smogAvailable) {
            sendCommand(O_COM_NSG, "0\n");

            if(receiveCommand().equals(I_COM_NSG + ":0")) {
                smogAvailable = false;
                nSensors--;
            }
        }

        if(airQualityAvailable) {
            sendCommand(O_COM_NAQ, "0\n");

            if(receiveCommand().equals(I_COM_NAQ + ":0")) {
                airQualityAvailable = false;
                nSensors--;
            }
        }
    }

    /**
     * Integrates and returns all the sensor values
     * arg: None
     * exception: IOException
     * return: Integer Array with mapped sensor values
     */

    public int[] getSensorValues() throws IOException {
        String temp;
        int[] sensorValues = new int[nSensors];
        int sensorValuesIndex = 0;

        sensorValues[sensorValuesIndex++] = Integer.parseInt(getSmogValue());

        sensorValues[sensorValuesIndex++] = getAirQualityValue();

        return sensorValues;
    }


    /**
     * Returns the current Smog sensor value
     * arg: None
     * exception: IOException
     * return: Integer
     */


    public String getSmogValue() throws IOException {
        Log.v(String.valueOf(authenticationStatus),String.valueOf(smogAvailable));
        if(authenticationStatus && smogAvailable) {

            String temp;
            sendCommand(O_COM_SSG, "\n");
            temp = receiveCommand();
            Log.v("STMCommunicator", "Message Received in getSmogValue(): " + temp);

            return temp.substring(5, temp.length());
            }
            return "0";
    }


    /**
     * Returns the current Air Quality sensor value
     * arg: None
     * exception: IOException
     * return: Integer
     */

    public int getAirQualityValue() throws IOException {
        if(authenticationStatus && airQualityAvailable) {
            String temp;
            sendCommand(O_COM_SAQ, "\n");
            temp = receiveCommand();

            if(temp.substring(0, 3).equals(I_COM_SAQ)) {
                return (int) temp.charAt(5);
            }
            return 0;
        }
        return 0;
    }
}
