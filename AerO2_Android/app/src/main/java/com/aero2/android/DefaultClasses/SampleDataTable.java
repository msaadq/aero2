package com.aero2.android.DefaultClasses;

/**
 * Represents a Row in Mapping Table
 *
 * Created by Saad on 12/8/2015.
 */

public class SampleDataTable {

    /**
     * SampleDataTable ID
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    /**
     * SampleDataTable Smog Value
     */
    @com.google.gson.annotations.SerializedName("smog")
    private double mSmog;

    /**
     * SampleDataTable Longitude
     */
    @com.google.gson.annotations.SerializedName("long")
    private double mLong;

    /**
     * SampleDataTable Latitude
     */
    @com.google.gson.annotations.SerializedName("lat")
    private double mLat;

    /**
     * SampleDataTable Altitude
     */
    @com.google.gson.annotations.SerializedName("alt")
    private double mAlt;

    /**
     * SampleDataTable Time
     */
    @com.google.gson.annotations.SerializedName("time")
    private double mTime;


    /**
     * SampleDataTable Normalized
     */
    @com.google.gson.annotations.SerializedName("normalized")
    private boolean mNormalized;


    /**
     * DBListItem constructor
     */
    public SampleDataTable() {
    }

    /**
     * Initializes a new SampleDataTable Row
     */
    public SampleDataTable(String id, double[] integratedValues) {
        this.setmId(id);
        this.setTime(integratedValues[0]);
        this.setLat(integratedValues[1]);
        this.setLong(integratedValues[2]);
        this.setAlt(integratedValues[3]);
        this.setSmog(integratedValues[4]);
        this.setmNormalized(false);
    }

    /**
     * Sets the SampleDataTable ID
     */
    public void setmId(String mId) {
        this.mId = mId;
    }

    /**
     * Sets the SampleDataTable Smog Value
     */
    public final void setSmog(double smog) {
        mSmog = smog;
    }

    /**
     * Sets the SampleDataTable Longitude
     */
    public final void setLong(double lon) {
        mLong = lon;
    }

    /**
     * Sets the SampleDataTable Latitude
     */
    public final void setLat(double lat) {
        mLat = lat;
    }

    /**
     * Sets the SampleDataTable Altitude
     */
    public final void setAlt(double alt) {
        mAlt = alt;
    }

    /**
     * Sets the SampleDataTable Time
     */
    public final void setTime(double time) {
        mTime = time;
    }

    public void setmNormalized(boolean Normalized) {
        mNormalized = Normalized;
    }

    /**
     * Returns the SampleDataTable ID
     */


    public String getmId() {
        return mId;
    }

    /**
     * Returns the SampleDataTable Smog Value
     */
    public double getSmog() {
        return mSmog;
    }

    /**
     * Returns the SampleDataTable Longitude
     */
    public double getLong() {
        return mLong;
    }

    /**
     * Returns the SampleDataTable Latitude
     */
    public double getLat() {
        return mLat;
    }

    /**
     * Returns the SampleDataTable Altitude
     */
    public double getAlt() {
        return mAlt;
    }

    /**
     * Returns the SampleDataTable Time
     */
    public double getTime() {
        return mTime;
    }


    public boolean getmNormalized() {
        return mNormalized;
    }
}