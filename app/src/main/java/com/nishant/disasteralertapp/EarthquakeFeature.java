package com.nishant.disasteralertapp;

import com.google.gson.annotations.SerializedName;

public class EarthquakeFeature {
    @SerializedName("geometry")
    private EarthquakeGeometry geometry;

    @SerializedName("properties")
    private EarthquakeProperties properties;

    public EarthquakeGeometry getGeometry() {
        return geometry;
    }

    public EarthquakeProperties getProperties() {
        return properties;
    }
}
