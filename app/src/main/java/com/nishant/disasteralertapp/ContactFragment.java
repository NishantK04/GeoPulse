package com.nishant.disasteralertapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class ContactFragment extends Fragment {

    private static final int REQUEST_PERMISSIONS = 100;
    private static final String PREF_NAME = "SOS_Prefs";
    private static final String KEY_PHONE = "emergency_contact";

    private Button btnSOS, btnCallEmergency;
    private ImageButton btnSettings;
    private TextView txtInternetStatus;
    private FusedLocationProviderClient fusedLocationClient;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        btnSOS = view.findViewById(R.id.btnSOS);
        btnSOS.setBackgroundResource(R.drawable.round_sos_button);
        btnCallEmergency = view.findViewById(R.id.btnCallEmergency);
        btnSettings = view.findViewById(R.id.btnSettings);
        txtInternetStatus = view.findViewById(R.id.txtInternetStatus);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        checkInternetStatus();

        btnSOS.setOnClickListener(v -> sendEmergencyAlert());
        btnCallEmergency.setOnClickListener(v -> callEmergencyNumber());
        btnSettings.setOnClickListener(v -> openSettingsFragment());

        return view;
    }

    private void checkInternetStatus() {
        boolean isConnected = NetworkUtils.isInternetAvailable(requireContext());
        txtInternetStatus.setText(isConnected ? "Internet Available" : "Offline Mode");
    }

    private void sendEmergencyAlert() {
        String emergencyContact = sharedPreferences.getString(KEY_PHONE, "");
        if (TextUtils.isEmpty(emergencyContact)) {
            Toast.makeText(getActivity(), "No emergency contact saved!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!hasRequiredPermissions()) {
            requestPermissions();
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                String message = "SOS! I need help. My location: " + getLocationLink(location);
                sendSMS(emergencyContact, message);
                sendWhatsAppMessage(emergencyContact, message);
            } else {
                Toast.makeText(getActivity(), "Could not get location!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean hasRequiredPermissions() {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        requestPermissions(new String[]{
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CALL_PHONE
        }, REQUEST_PERMISSIONS);
    }

    private void sendSMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getActivity(), "SOS Sent via SMS to " + phoneNumber, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "SMS failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("SMS_SEND_ERROR", "Failed to send SMS: " + e.getMessage());
        }
    }

    private void sendWhatsAppMessage(String phoneNumber, String message) {
        try {
            phoneNumber = phoneNumber.replace("+", "").replace(" ", "");  // Ensure correct format
            String whatsappUrl = "https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + Uri.encode(message);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(whatsappUrl));
            startActivity(intent);
            Toast.makeText(getActivity(), "SOS Sent via WhatsApp", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "WhatsApp message failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("WHATSAPP_ERROR", "Failed to send WhatsApp message: " + e.getMessage());
        }
    }

    private String getLocationLink(Location location) {
        return "https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
    }

    private void callEmergencyNumber() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:112"));
        startActivity(callIntent);
    }

    private void openSettingsFragment() {
        FragmentTransaction transaction = requireActivity()
                .getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.fragment_container, new EmergencySettingsFragment());
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            boolean allGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                Toast.makeText(getActivity(), "Permissions granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Permissions denied. Please allow permissions in settings.", Toast.LENGTH_LONG).show();
            }
        }
    }
}

