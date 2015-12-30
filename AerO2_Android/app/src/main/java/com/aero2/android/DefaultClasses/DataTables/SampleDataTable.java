package com.aero2.android.DefaultClasses.DataTables;

/**
 * Represents a Row in SampleDataTable
 *
 * Created by Saad on 12/8/2015.
 */

public class SampleDataTable {

    // SampleDataTable Attributes

    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    @com.google.gson.annotations.SerializedName("time")
    private Double mTime;

    @com.google.gson.annotations.SerializedName("long")
    private Double mLong;

    @com.google.gson.annotations.SerializedName("lat")
    private Double mLat;

    @com.google.gson.annotations.SerializedName("alt")
    private Double mAlt;

    @com.google.gson.annotations.SerializedName("smog")
    private Double mSmog;


    @com.google.gson.annotations.SerializedName("normalized")
    private Double mNormalized;



    public SampleDataTable() {
    }

    /**
     * Initializes a new SampleDataTable Row
     */

    public SampleDataTable(String id, Double[] integratedValues) {
        this.setId(id);
        this.setTime(integratedValues[0]);
        this.setLat(integratedValues[1]);
        this.setLong(integratedValues[2]);
        this.setAlt(integratedValues[3]);
        this.setSmog(integratedValues[4]);
        this.setNormalized(integratedValues[5]);
    }

    // SampleDataTable Setters

    public void setId(String mId) { this.mId = mId; }

    public final void setTime(Double time) { mTime = time; }

    public final void setLong(Double lon) { mLong = lon; }

    public final void setLat(Double lat) { mLat = lat; }

    public final void setAlt(Double alt) {
        mAlt = alt;
    }

    public final void setSmog(Double smog) {
        mSmog = smog;
    }

    public final void setNormalized(Double normalized) { mNormalized = normalized; }

    // SampleDataTable Getters

    public String getId() { return mId; }

    public Double getTime() { return mTime; }

    public Double getLong() { return mLong; }

    public Double getLat() { return mLat;}

    public Double getAlt() {
        return mAlt;
    }

    public Double getSmog() {
        return mSmog;
    }

    public Double getNormalized() {
        return mNormalized;
    }



}