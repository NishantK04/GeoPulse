package com.nishant.disasteralertapp;

import com.google.gson.annotations.SerializedName;

public class EarthquakeGeometry {

    @SerializedName("coordinates")
    private double[] coordinates;  // Array for [longitude, latitude, depth]

    public double getLongitude() {
        return coordinates[0];
    }

    public double getLatitude() {
        return coordinates[1];
    }

    public double getDepth() {
        return coordinates[2];
    }
}
