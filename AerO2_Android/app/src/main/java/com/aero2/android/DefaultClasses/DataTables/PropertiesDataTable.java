package com.aero2.android.DefaultClasses.DataTables;

/**
 * Represents a Row in PropertiesDataTable
 *
 * Created by Usman on 12/29/2015.
 */

public class PropertiesDataTable {

    // PropertiesDataTable Attributes

    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    @com.google.gson.annotations.SerializedName("time")
    private Double mTime;

    @com.google.gson.annotations.SerializedName("lat")
    private Double mLat;

    @com.google.gson.annotations.SerializedName("long")
    private Double mLong;

    @com.google.gson.annotations.SerializedName("alt")
    private Double mAlt;

    @com.google.gson.annotations.SerializedName("r_index")
    private Double mrIndex;

    @com.google.gson.annotations.SerializedName("i_index")
    private Double miINDEX;

    @com.google.gson.annotations.SerializedName("sampled")
    private Double mSampled;


    public PropertiesDataTable() {
    }

    /**
     * Initializes a new PropertiesDataTable Row
     */

    public PropertiesDataTable(String id, Double mTime, Double mLat, Double mLong,
                               Double mAlt, Double mrIndex, Double miIndex,
                               Double mSampled) {

        this.setId(id);
        this.setTime(mTime);
        this.setLat(mLat);
        this.setLong(mLong);
        this.setAlt(mAlt);
        this.setrIndex(mrIndex);
        this.setiIndex(miIndex);
        this.setSampled(mSampled);
    }

    //PropertiesDataTable Getters

    public String getId() {
        return mId;
    }

    public Double getTime() {
        return mTime;
    }

    public Double getLat() {
        return mLat;
    }

    public Double getLong() {
        return mLong;
    }

    public Double getAlt() {
        return mAlt;
    }

    public Double getrIndex() {
        return mrIndex;
    }

    public Double getiIndex() {
        return miINDEX;
    }

    public Double getSampled() {
        return mSampled;
    }

    //PropertiesDataTableSetters

    public void setId(String mId) {
        this.mId = mId;
    }

    public void setTime(Double mTime) {
        this.mTime = mTime;
    }

    public void setLat(Double mLat) {
        this.mLat = mLat;
    }

    public void setAlt(Double mAlt) {
        this.mAlt = mAlt;
    }

    public void setLong(Double mLong) {
        this.mLong = mLong;
    }

    public void setrIndex(Double mrIndex) {
        this.mrIndex = mrIndex;
    }

    public void setiIndex(Double miINDEX) {
        this.miINDEX = miINDEX;
    }

    public void setSampled(Double mSampled) {
        this.mSampled = mSampled;
    }
}