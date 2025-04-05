package com.nishant.disasteralertapp;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/";
    private static Retrofit retrofit = null;
    private static final String BASE_URL1 = "https://your-api-name.onrender.com/";


    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Add this method to get the EarthquakeApiService
    public EarthquakeApiService getEarthquakeApiService() {
        return getClient().create(EarthquakeApiService.class);
    }

    public static AiApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL1)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(AiApiService.class);
    }
}


