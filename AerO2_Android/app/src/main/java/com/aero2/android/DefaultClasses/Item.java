package com.aero2.android.DefaultClasses;

/**
 * Represents a Row in Mapping Table
 *
 * Created by Saad on 12/8/2015.
 */

public class Item {

    /**
     * Item ID
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    /**
     * Item Smog Value
     */
    @com.google.gson.annotations.SerializedName("smog")
    private String mSmog;

    /**
     * Item Longitude
     */
    @com.google.gson.annotations.SerializedName("long")
    private String mLong;


    /**
     * Item Latitude
     */
    @com.google.gson.annotations.SerializedName("lat")
    private String mLat;


    /**
     * Item Altitude
     */
    @com.google.gson.annotations.SerializedName("alt")
    private String mAlt;

    /**
     * Item Time
     */
    @com.google.gson.annotations.SerializedName("time")
    private String mTime;

    /**
     * DBListItem constructor
     */
    public Item() { }

    /**
     * Initializes a new DBListItem
     */
    public Item(String id, double[] integratedValues) {
        this.setmId(id);
        this.setSmog(integratedValues[0]);
        this.setLong(integratedValues[1]);
        this.setLat(integratedValues[2]);
        this.setAlt(integratedValues[3]);
        this.setTime(integratedValues[4]);
    }


    /**
     * Sets the ID
     */
    public void setmId(String mId) {
        this.mId = mId;
    }

    /**
     * Sets the item Smog Value
     */
    public final void setSmog(double smog) {
        mSmog = String.valueOf(smog);
    }

    /**
     * Sets the item Longitude
     */
    public final void setLong(double lon) {
        mLong = String.valueOf(lon);
    }

    /**
     * Sets the item Latitude
     */
    public final void setLat(double lat) {
        mLat = String.valueOf(lat);
    }

    /**
     * Sets the item Altitude
     */
    public final void setAlt(double alt) {
        mAlt = String.valueOf(alt);
    }

    /**
     * Sets the item Time
     */
    public final void setTime(double time) {
        mTime = String.valueOf(time);
    }

    /**
     * Returns the ID
     */
    public String getmId() {
        return mId;
    }

    /**
     * Returns the item Smog Value
     */
    public String getSmog() {
        return mSmog;
    }

    /**
     * Returns the item Longitude
     */
    public String getLong() {
        return mLong;
    }

    /**
     * Returns the item Latitude
     */
    public String getLat() {
        return mLat;
    }

    /**
     * Returns the item Altitude
     */
    public String getAlt() {
        return mAlt;
    }

    /**
     * Returns the item Time
     */
    public String getTime() {
        return mTime;
    }
}
