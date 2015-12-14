package com.aero2.android.DefaultClasses;

import android.app.Activity;

import java.io.IOException;
import java.util.Date;

/**
 *
 * Instantiates STMCommunicator's and GPSTracker's objects and
 * integrates them.
 *
 * Created by Usman on 11/17/2015.
 */
public class Integrator {

    private STMCommunicator sensor;
    private GPSTracker gps;

    Integrator(Activity activity){
        try{
            sensor = new STMCommunicator(activity);
            gps = new GPSTracker(activity);
        }
        catch (IOException ie){
            ie.printStackTrace();
        }
    }

    /**
     * Integrates Smog with GPS and time
     * values.
     * arg: None
     * exception: None
     * return: Double array containing smog,
     * longitude, latitude, altitude and time (in order)
     */

    public double[] integrateSmog(){

        double [] integrated = {};
        Date date = new Date();

        try {
            int smog = sensor.getSmogValue();
            double[] newLocation = gps.getGps();
            int temp = integrated.length;
            integrated = new double[temp + 2];

            System.arraycopy(smog,0,integrated,0,1);
            System.arraycopy(newLocation,0,integrated,1,temp);
            System.arraycopy(date,0,integrated,1+temp,1);
        }
        catch (IOException ie){
            ie.printStackTrace();
        }

        return integrated;
    }

    public SampleDataTable integratedAir(){
        SampleDataTable data=new SampleDataTable();
        Date date=new Date();
        try {
            double smog=sensor.getSmogValue();
            double airQuality=sensor.getAirQualityValue();
            double time=date.getTime();
            double longi=gps.getGps()[0];
            double lat=gps.getGps()[1];
            double alt=gps.getGps()[2];
            data.setmId(String.valueOf(time));
            data.setSmog(smog);
            data.setmAirQ(airQuality);
            data.setTime(time);
            data.setLong(longi);
            data.setLat(lat);
            data.setAlt(alt);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
