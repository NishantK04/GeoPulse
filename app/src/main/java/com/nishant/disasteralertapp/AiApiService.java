package com.nishant.disasteralertapp;


import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AiApiService {
    @Headers("Content-Type: application/json")
    @POST("predict")
    Call<JsonObject> getPrediction(@Body JsonObject requestBody);
}