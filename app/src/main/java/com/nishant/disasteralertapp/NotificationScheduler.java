package com.nishant.disasteralertapp;

import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

public class NotificationScheduler {

    public static void scheduleEarthquakeNotification(Context context, double latitude, double longitude) {
        // Set up the input data (latitude and longitude)
        Data inputData = new Data.Builder()
                .putDouble("latitude", latitude)
                .putDouble("longitude", longitude)
                .build();

        // Create the work request to check for earthquake
        WorkRequest earthquakeWorkRequest = new OneTimeWorkRequest.Builder(EarthquakeWorker.class)
                .setInputData(inputData)
                .build();

        // Enqueue the work request
        WorkManager.getInstance(context).enqueue(earthquakeWorkRequest);
    }
}
