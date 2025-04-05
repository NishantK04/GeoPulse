package com.nishant.disasteralertapp;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EarthquakeResponse {
    @SerializedName("features")
    private List<EarthquakeFeature> features;

    public List<EarthquakeFeature> getFeatures() {
        return features;
    }
}

