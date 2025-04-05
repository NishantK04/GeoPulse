package com.nishant.disasteralertapp;

public class AiPredictionRequest {
    private double magnitude;
    private double depth;
    private double latitude;
    private double longitude;
    private double time_between_shocks;

    public AiPredictionRequest(double magnitude, double depth, double latitude, double longitude, double time_between_shocks) {
        this.magnitude = magnitude;
        this.depth = depth;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time_between_shocks = time_between_shocks;
    }
}
