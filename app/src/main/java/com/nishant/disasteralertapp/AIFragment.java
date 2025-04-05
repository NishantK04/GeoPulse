package com.nishant.disasteralertapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AIFragment extends Fragment {

    private TextView txtPrediction;
    private Button btnPredict, btnFetchPredict;
    private RequestQueue requestQueue;
    private FusedLocationProviderClient fusedLocationClient;
    private double userLatitude, userLongitude;
    private JSONObject nearestEarthquake;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public AIFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai, container, false);

        btnPredict = view.findViewById(R.id.btnPredict);
        btnFetchPredict = view.findViewById(R.id.btnFetchPredict);
        txtPrediction = view.findViewById(R.id.txtPrediction);

        requestQueue = Volley.newRequestQueue(requireContext());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        btnFetchPredict.setOnClickListener(v -> fetchUserLocation());
        btnPredict.setOnClickListener(v -> makePrediction());

        return view;
    }

    @SuppressLint("MissingPermission")
    private void fetchUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                userLatitude = location.getLatitude();
                userLongitude = location.getLongitude();
                executorService.execute(this::fetchNearestEarthquake);
            } else {
                txtPrediction.setText("Unable to fetch location");
            }
        });
    }

    private void fetchNearestEarthquake() {
        String usgsUrl = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, usgsUrl, null,
                response -> executorService.execute(() -> parseEarthquakeData(response)),
                error -> txtPrediction.setText("Error fetching earthquake data: " + error.getMessage())
        );
        requestQueue.add(request);
    }

    private void fetchCountry(double latitude, double longitude, double magnitude) {
        String geocodingUrl = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + latitude + "&lon=" + longitude;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, geocodingUrl, null,
                response -> {
                    try {
                        String country = response.has("address") && response.getJSONObject("address").has("country") ?
                                response.getJSONObject("address").getString("country") : "Unknown Country";

                        updateUIText("Nearest Earthquake: Magnitude " + magnitude + " in " + country);
                    } catch (JSONException e) {
                        updateUIText("Error fetching country");
                    }
                },
                error -> updateUIText("Error getting location data")
        );

        requestQueue.add(request);
    }


    private void parseEarthquakeData(JSONObject response) {
        try {
            JSONArray features = response.getJSONArray("features");
            double minDistance = Double.MAX_VALUE;

            for (int i = 0; i < features.length(); i++) {
                JSONObject earthquake = features.getJSONObject(i);
                JSONObject geometry = earthquake.getJSONObject("geometry");
                double eqLongitude = geometry.getJSONArray("coordinates").getDouble(0);
                double eqLatitude = geometry.getJSONArray("coordinates").getDouble(1);

                double distance = calculateDistance(userLatitude, userLongitude, eqLatitude, eqLongitude);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestEarthquake = earthquake;
                }
            }

            if (nearestEarthquake != null) {
                JSONObject properties = nearestEarthquake.getJSONObject("properties");
                double magnitude = properties.getDouble("mag");

                // Get the location of the earthquake
                JSONObject geometry = nearestEarthquake.getJSONObject("geometry");
                double eqLatitude = geometry.getJSONArray("coordinates").getDouble(1);
                double eqLongitude = geometry.getJSONArray("coordinates").getDouble(0);

                // Fetch country using Reverse Geocoding
                fetchCountry(eqLatitude, eqLongitude, magnitude);
            } else {
                updateUIText("No nearby earthquakes found");
            }
        } catch (JSONException e) {
            updateUIText("JSON Error: " + e.getMessage());
        }
    }


    private void makePrediction() {
        if (nearestEarthquake == null) {
            txtPrediction.setText("Please fetch earthquake data first");
            return;
        }
        try {
            JSONObject properties = nearestEarthquake.getJSONObject("properties");
            JSONObject geometry = nearestEarthquake.getJSONObject("geometry");

            double magnitude = properties.getDouble("mag");
            double longitude = geometry.getJSONArray("coordinates").getDouble(0);
            double latitude = geometry.getJSONArray("coordinates").getDouble(1);
            double depth = geometry.getJSONArray("coordinates").getDouble(2);
            double timeBetweenShocks = 10.0;

            executorService.execute(() -> sendPredictionRequest(magnitude, depth, latitude, longitude, timeBetweenShocks));
        } catch (JSONException e) {
            txtPrediction.setText("Error parsing earthquake data");
        }
    }

    private void sendPredictionRequest(double magnitude, double depth, double latitude, double longitude, double timeBetweenShocks) {
        String predictionUrl = "https://earthquakeapi-x6pt.onrender.com/predict";

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("magnitude", magnitude);
            requestBody.put("depth", depth);
            requestBody.put("latitude", latitude);
            requestBody.put("longitude", longitude);
            requestBody.put("time_between_shocks", timeBetweenShocks);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, predictionUrl, requestBody,
                    response -> {
                        try {
                            if (response.has("predicted_magnitude")) {
                                double predictedMagnitude = response.getDouble("predicted_magnitude");
                                updateUIText("Predicted Magnitude: " + predictedMagnitude);
                            } else {
                                updateUIText("Prediction not found in response");
                            }
                        } catch (JSONException e) {
                            updateUIText("Error parsing prediction response");
                        }
                    },
                    error -> updateUIText("Error getting prediction: " + error.getMessage())) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            requestQueue.add(request);
        } catch (JSONException e) {
            updateUIText("JSON Error: " + e.getMessage());
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private void updateUIText(String text) {
        requireActivity().runOnUiThread(() -> txtPrediction.setText(text));
    }
}
