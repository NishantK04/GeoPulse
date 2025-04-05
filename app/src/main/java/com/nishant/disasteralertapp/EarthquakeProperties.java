package com.nishant.disasteralertapp;

import com.google.gson.annotations.SerializedName;

public class EarthquakeProperties {

    @SerializedName("mag")
    private double magnitude;

    @SerializedName("place")
    private String place;

    @SerializedName("time")
    private long time;  // Unix timestamp (milliseconds)

    @SerializedName("url")
    private String url;

    public double getMag() {
        return magnitude;
    }

    public String getPlace() {
        return place;
    }

    public long getTime() {
        return time;
    }

    public String getUrl() {
        return url;
    }

    public double getMagnitude() {
        return magnitude;
    }
}
