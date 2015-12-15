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
    private String mSmog;

    /**
     * SampleDataTable Longitude
     */
    @com.google.gson.annotations.SerializedName("long")
    private String mLong;

    /**
     * SampleDataTable Latitude
     */
    @com.google.gson.annotations.SerializedName("lat")
    private String mLat;

    /**
     * SampleDataTable Altitude
     */
    @com.google.gson.annotations.SerializedName("alt")
    private String mAlt;

    /**
     * SampleDataTable Time
     */
    @com.google.gson.annotations.SerializedName("time")
    private String mTime;


    /**
     * SampleDataTable Normalized
     */
    @com.google.gson.annotations.SerializedName("normalized")
    private Boolean mNormalized;

    /**
     * DBListItem constructor
     */
    public SampleDataTable() {
    }

    /**
     * Initializes a new SampleDataTable Row
     */
    public SampleDataTable(String id, String[] integratedValues) {
        this.setmId(id);
        this.setTime(integratedValues[0]);
        this.setLat(integratedValues[1]);
        this.setLong(integratedValues[2]);
        this.setAlt(integratedValues[3]);
        this.setSmog(integratedValues[4]);
        this.setmNormalized(integratedValues[5]);
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
    public final void setSmog(String smog) {
        mSmog = smog;
    }

    /**
     * Sets the SampleDataTable Longitude
     */
    public final void setLong(String lon) {
        mLong = lon;
    }

    /**
     * Sets the SampleDataTable Latitude
     */
    public final void setLat(String lat) {
        mLat = lat;
    }

    /**
     * Sets the SampleDataTable Altitude
     */
    public final void setAlt(String alt) {
        mAlt = alt;
    }

    /**
     * Sets the SampleDataTable Time
     */
    public final void setTime(String time) {
        mTime = time;
    }

    public void setmNormalized(String mNormalized) {
        mNormalized = String.valueOf(mNormalized);
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
    public String getSmog() {
        return mSmog;
    }

    /**
     * Returns the SampleDataTable Longitude
     */
    public String getLong() {
        return mLong;
    }

    /**
     * Returns the SampleDataTable Latitude
     */
    public String getLat() {
        return mLat;
    }

    /**
     * Returns the SampleDataTable Altitude
     */
    public String getAlt() {
        return mAlt;
    }

    /**
     * Returns the SampleDataTable Time
     */
    public String getTime() {
        return mTime;
    }


    public Boolean getmNormalized() {
        return mNormalized;
    }
}