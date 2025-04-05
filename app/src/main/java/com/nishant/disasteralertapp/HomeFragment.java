package com.nishant.disasteralertapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final float SAFE_ZONE_THRESHOLD = 50.0f; // 50 km threshold for safe zone
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mMap;
    private double userLatitude;
    private double userLongitude;
    private TextView safetyTextView;
    private double nearestEarthquakeLatitude = 0;
    private double nearestEarthquakeLongitude = 0;
    private float nearestEarthquakeDistance = Float.MAX_VALUE;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        safetyTextView = view.findViewById(R.id.safetyTextView);


        safetyTextView.setText("You are safe now. No earthquakes happening.");

        // Check for location permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());


            getUserLocation();
        } else {

            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        // Get the map fragment and set up the map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    private void getUserLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        userLatitude = location.getLatitude();
                        userLongitude = location.getLongitude();

                        // Now call the API to get earthquake data
                        getEarthquakeData();
                        Log.d("HomeFragment", "User location: Latitude " + userLatitude + ", Longitude " + userLongitude);
                    } else {
                        Toast.makeText(getContext(), "Unable to get location", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getEarthquakeData() {
        EarthquakeApiService apiService = RetrofitClient.getClient().create(EarthquakeApiService.class);
        apiService.getRecentEarthquakes().enqueue(new Callback<EarthquakeResponse>() {
            @Override
            public void onResponse(Call<EarthquakeResponse> call, Response<EarthquakeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EarthquakeFeature> earthquakeFeatures = response.body().getFeatures();
                    Log.d("HomeFragment", "Received " + earthquakeFeatures.size() + " earthquakes.");

                    if (earthquakeFeatures.isEmpty()) {
                        Log.d("HomeFragment", "No earthquakes found.");
                    }

                    // Iterate through all the earthquakes to find the nearest one
                    for (EarthquakeFeature feature : earthquakeFeatures) {
                        EarthquakeGeometry geometry = feature.getGeometry();
                        double latitude = geometry.getLatitude();
                        double longitude = geometry.getLongitude();
                        double magnitude = feature.getProperties().getMag();  // Earthquake magnitude

                        // Calculate distance from user's location to the earthquake location
                        float[] results = new float[1];
                        Location.distanceBetween(userLatitude, userLongitude, latitude, longitude, results);
                        float distanceInKm = results[0] / 1000; // Convert to kilometers

                        // Find the nearest earthquake
                        if (distanceInKm < nearestEarthquakeDistance) {
                            nearestEarthquakeDistance = distanceInKm;
                            nearestEarthquakeLatitude = latitude;
                            nearestEarthquakeLongitude = longitude;
                        }

                        // Determine the marker color based on the earthquake's magnitude
                        float markerColor;
                        if (magnitude >= 6.0) {
                            markerColor = BitmapDescriptorFactory.HUE_RED;  // High risk (Red)
                        } else if (magnitude >= 4.0) {
                            markerColor = BitmapDescriptorFactory.HUE_YELLOW;  // Moderate risk (Yellow)
                        } else {
                            markerColor = BitmapDescriptorFactory.HUE_BLUE;  // Minor risk (Green)
                        }

                        // Add a marker with the appropriate color based on the magnitude
                        Log.d("HomeFragment", "Adding marker at " + latitude + ", " + longitude);
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .title("Earthquake at " + latitude + ", " + longitude + "\nMagnitude: " + magnitude)
                                .icon(BitmapDescriptorFactory.defaultMarker(markerColor))); // Marker with the selected color
                    }

                    // Determine if the user is in a safe zone
                    String safetyText = "User Location: " + userLatitude + ", " + userLongitude + "\n";
                    if (nearestEarthquakeDistance <= SAFE_ZONE_THRESHOLD) {
                        safetyText += "Zone: Not Safe\n";  // User is in a danger zone if earthquake is nearby
                    } else {
                        safetyText += "Zone: Safe\n";  // User is safe if no earthquakes nearby
                    }

                    // Update TextView with earthquake details
                    safetyText += "Distance to nearest earthquake: " + nearestEarthquakeDistance + " km\n";
                    safetyTextView.setText(safetyText);

                    // Draw a red line from user to nearest earthquake
                    LatLng earthquakeLocation = new LatLng(nearestEarthquakeLatitude, nearestEarthquakeLongitude);
                    mMap.addPolyline(new PolylineOptions()
                            .add(new LatLng(userLatitude, userLongitude), earthquakeLocation)
                            .color(getResources().getColor(R.color.red))  // Red line
                            .width(5));  // Line width
                } else {
                    Log.e("HomeFragment", "Failed to retrieve data, response code: " + response.code());
                    Toast.makeText(getContext(), "Failed to retrieve earthquake data.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EarthquakeResponse> call, Throwable t) {
                Toast.makeText(getContext(), "API request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("HomeFragment", "API request failed: " + t.getMessage(), t);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();  // Permission granted, get the user location
            } else {
                Toast.makeText(getContext(), "Location permission is required to fetch data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Enable the "My Location" button to show the user's current position
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true); // Show the user's location on the map
        } else {
            Log.e("HomeFragment", "Location permission not granted.");
            return;
        }

        // Check if the user location is available
        if (userLatitude != 0 && userLongitude != 0) {
            LatLng userLocation = new LatLng(userLatitude, userLongitude);

            // Add a marker for the user's location
            mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));

            // Move the camera to the user's location with a zoom level that fits the user's surroundings
            float zoomLevel = 12.0f; // Adjust this zoom level as needed
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, zoomLevel));
        } else {
            Log.e("HomeFragment", "User location not set properly.");
        }

        // Zoom out and center the map on India with a very low zoom level
        if (userLatitude == 0 && userLongitude == 0) {
            LatLng india = new LatLng(20.5937, 78.9629);  // Coordinates for India
            float zoomLevel = 2.0f; // Very zoomed-out view of the entire world
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(india, zoomLevel));
        }

        // Enable zoom controls on the map
        mMap.getUiSettings().setZoomControlsEnabled(true); // Show zoom in and zoom out buttons
    }

}
