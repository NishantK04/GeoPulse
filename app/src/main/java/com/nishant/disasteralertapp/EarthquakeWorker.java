package com.nishant.disasteralertapp;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class EarthquakeWorker extends Worker {

    public EarthquakeWorker(@NonNull Context context, @NonNull WorkerParameters workerParameters) {
        super(context, workerParameters);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "EarthquakeWorker triggered");
        // Fetch the current location (latitude and longitude) from input data
        double latitude = getInputData().getDouble("latitude", 0.0);
        double longitude = getInputData().getDouble("longitude", 0.0);

        // Fetch the earthquake data using Retrofit
        RetrofitClient retrofitClient = new RetrofitClient();
        retrofitClient.getEarthquakeApiService()  // Get the EarthquakeApiService
                .getRecentEarthquakes()  // Call the method to get recent earthquake data
                .enqueue(new Callback<EarthquakeResponse>() {
                    @Override
                    public void onResponse(Call<EarthquakeResponse> call, Response<EarthquakeResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            EarthquakeResponse earthquakeResponse = response.body();
                            // Iterate through each earthquake feature in the response
                            for (EarthquakeFeature eqFeature : earthquakeResponse.getFeatures()) {
                                double latitudeEq = eqFeature.getGeometry().getLatitude();
                                double longitudeEq = eqFeature.getGeometry().getLongitude();
                                double magnitude = eqFeature.getProperties().getMagnitude();

                                // Calculate the distance between the user's location and the earthquake location
                                double distance = calculateDistance(latitude, longitude, latitudeEq, longitudeEq);

                                if (distance <= 100) {  // Example: Check if earthquake is within 100 km
                                    sendNotification(magnitude);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<EarthquakeResponse> call, Throwable t) {
                        // Handle failure (e.g., no internet)
                    }
                });

        return Result.success();
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula to calculate the distance between two coordinates
        final int R = 6371; // Radius of Earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in km
    }

    private void sendNotification(double magnitude) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "earthquake_channel")
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Earthquake Alert!")
                .setContentText("An earthquake of magnitude " + magnitude + " occurred near your location.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(1, builder.build());
    }
}

