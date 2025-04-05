package com.nishant.disasteralertapp;

import retrofit2.Call;
import retrofit2.http.GET;

public interface EarthquakeApiService {

    // Modify this endpoint to match the correct API route
    @GET("summary/all_hour.geojson")
    Call<EarthquakeResponse> getRecentEarthquakes();
}
