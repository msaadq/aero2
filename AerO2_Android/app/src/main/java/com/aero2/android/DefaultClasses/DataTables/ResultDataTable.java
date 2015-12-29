package com.aero2.android.DefaultClasses.DataTables;

/**
 * Represents a Row in Mapping Table
 *
 * Created by Saad on 12/8/2015.
 */

public class ResultDataTable {

    // ResultDataTable Attributes

    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    @com.google.gson.annotations.SerializedName("time")
    private Double mTime;

    @com.google.gson.annotations.SerializedName("lat")
    private Double mLat;

    @com.google.gson.annotations.SerializedName("long")
    private Double mLong;

    @com.google.gson.annotations.SerializedName("air_index")
    private Double mAirIndex;

    public ResultDataTable() {
    }

    /**
     * Initializes a new ResultDataTable Row
     */

    public ResultDataTable(String id, Double mTime, Double mLat, Double mLong,
                           Double mAirIndex) {

        this.setId(id);
        this.setTime(mTime);
        this.setLat(mLat);
        this.setLong(mLong);
        this.setAirIndex(mAirIndex);
    }

    //Getter Functions

    public String getId() {
        return mId;
    }

    public Double getLat() {
        return mLat;
    }

    public Double getLong() {
        return mLong;
    }

    public Double getAirIndex() {
        return mAirIndex;
    }

    // Setter Functions

    public void setLong(Double mLong) {
        this.mLong = mLong;
    }

    public void setLat(Double mLat) {
        this.mLat = mLat;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public Double getTime() {
        return mTime;
    }

    public void setTime(Double mTime) {
        this.mTime = mTime;
    }

    public void setAirIndex(Double mAirIndex) {
        this.mAirIndex = mAirIndex;
    }


}