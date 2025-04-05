package com.nishant.disasteralertapp;

import com.google.gson.annotations.SerializedName;

public class Earthquake {
    private double latitude;
    private double longitude;
    private double magnitude;

    // Constructor
    public Earthquake(double latitude, double longitude, double magnitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.magnitude = magnitude;
    }

    // Getter methods
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getMagnitude() {
        return magnitude;
    }
}
