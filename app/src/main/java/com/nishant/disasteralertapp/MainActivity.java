package com.nishant.disasteralertapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.LiveData;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.orange));






        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Get the current location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        // Get the user's current latitude and longitude
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        // Schedule the Earthquake worker
                        NotificationScheduler.scheduleEarthquakeNotification(this, latitude, longitude);
                    } else {
                        Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show();
                    }
                });

        NotificationHelper.createNotificationChannel(this);


        scheduleEarthquakeWorker();








        // Set up the window insets to ensure proper layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Load HomeFragment only once when activity is first created
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }


        // Set up bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.nav_ai) {
                selectedFragment = new AIFragment();
            } else if (item.getItemId() == R.id.nav_sos) {
                selectedFragment = new ContactFragment();
            } else if (item.getItemId() == R.id.nav_safety) {
                selectedFragment = new SafetyTipsFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });
    }
    // Method to schedule the periodic worker
    public void scheduleEarthquakeWorker() {
        // Create a one-time work request for the EarthquakeWorker
        PeriodicWorkRequest earthquakeWorkRequest =
                new PeriodicWorkRequest.Builder(EarthquakeWorker.class, 1, TimeUnit.MINUTES)
                        .build();



        // Enqueue the work request
        WorkManager.getInstance(getApplicationContext()).enqueue(earthquakeWorkRequest);

        // Observe the status of the worker (this will return a LiveData object)
        LiveData<WorkInfo> workInfoLiveData = WorkManager.getInstance(getApplicationContext())
                .getWorkInfoByIdLiveData(earthquakeWorkRequest.getId());

        // Observe work status
        workInfoLiveData.observe(this, workInfo -> {
            if (workInfo != null) {
                // Check if the work is finished
                if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                    Log.d("WorkManager", "Earthquake Worker finished successfully");
                } else if (workInfo.getState() == WorkInfo.State.FAILED) {
                    Log.d("WorkManager", "Earthquake Worker failed");
                } else if (workInfo.getState() == WorkInfo.State.RUNNING) {
                    Log.d("WorkManager", "Earthquake Worker is running");
                }
            }
        });
    }


}
