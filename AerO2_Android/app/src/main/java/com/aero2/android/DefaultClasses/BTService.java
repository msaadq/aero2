package com.aero2.android.DefaultClasses;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

/**
 * Enable the Android phone to connect to a generic Bluetooth Device and allows
 * the sending and receiving of Strings
 * over Bluetooth
 *
 * Created by Saad on 11/15/2015.
 */

public class BTService {
    static private OutputStream outputStream;
    static private InputStream inStream;
    static private Activity activity;

    static private BluetoothAdapter bluetoothAdapter;
    static private Set<BluetoothDevice> bondedDevices;

    static private BluetoothDevice device;
    static protected BluetoothSocket socket;

    private static String btDeviceName;

    private final static int REQUEST_ENABLE_BT = 1;

    /**
     * Initializes the BTService by calling
     * Pre-requisite functions
     * arg: The Current Activity, BT Device Name
     * exception: IOException
     * return: No return value.
     */

    public BTService(Activity activity, String deviceName) {
        Log.e("Appstatus", "Entered the BTService Constructor");

        this.activity = activity;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.btDeviceName = deviceName;

        try {
            setBluetoothDevice();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            bondDevice(deviceName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks for and Sets the BT Adapter inside Android. Turns it on if needed.
     * arg: None
     * exception: IOException
     * return: No return value.
     */

    private void setBluetoothDevice() throws IOException {
        Log.e("Appstatus", "Entered the setBluetoothDevice() function");

        if (bluetoothAdapter != null) {
            Log.e("Appstatus", "Bluetooth non null");

            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        else {
            Log.e("On Screen Message", "Your device does not support Bluetooth");
        }
    }

    /**
     * Connects to the BT device having the provided device name
     * arg: BT Device Name
     * exception: IOException
     * return: No return value.
     */

    public static void bondDevice(String deviceName) throws IOException{
        Log.e("Appstatus", "Entered the bondDevice() function");

        bondedDevices = bluetoothAdapter.getBondedDevices();
        Log.e("Appstatus", bondedDevices.size() + " is the no. of Devices connected");

        if(bondedDevices.size() > 0){
            Log.e("Appstatus", "Bonded Devices present");

            for (BluetoothDevice single_device : bondedDevices) {
                Log.e("checking for:  ", single_device.getName());

                if(single_device.getName().contains(deviceName)) {
                    Log.e("Connected to : ", single_device.getName());
                    device = single_device;
                }
            }

            ParcelUuid[] uuids = device.getUuids();
            socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
            socket.connect();
            outputStream = socket.getOutputStream();
            inStream = socket.getInputStream();
        }
        else {
            Log.e("On Screen Message", "No appropriate paired devices.");
        }
    }

    /**
     * Sends Message to the BT Device
     * arg: Message
     * exception: IOException
     * return: No return value.
     */

    public void sendMessage(String message) throws IOException {
        Log.e("Appstatus", "Entered the sendMessage() function");

        outputStream = socket.getOutputStream();
        outputStream.write(message.getBytes());
    }

    /**
     * Receives Message from the BT Device of the given Size
     * arg: Message Size
     * exception: IOException
     * return: No return value.
     */

    public String readMessage(int messageSize) throws IOException {
        Log.e("Appstatus", "Entered the readMessage() function");

        byte[] dataBuffer = new byte[messageSize];

        while(inStream.read(dataBuffer) >= 0);

        return new String(dataBuffer, "UTF-8");
    }
}
