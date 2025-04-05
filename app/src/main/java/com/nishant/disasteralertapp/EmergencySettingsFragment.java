package com.nishant.disasteralertapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EmergencySettingsFragment extends Fragment {

    private EditText editTextPhone;
    private Button btnSave;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "SOS_Prefs";
    private static final String KEY_PHONE = "emergency_contact";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emergency_settings, container, false);

        editTextPhone = view.findViewById(R.id.editTextPhone);
        btnSave = view.findViewById(R.id.btnSave);
        sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Load saved contact (but do not display it in the EditText)
        String savedNumber = sharedPreferences.getString(KEY_PHONE, "");

        btnSave.setOnClickListener(v -> {
            String phoneNumber = editTextPhone.getText().toString().trim();
            if (!TextUtils.isEmpty(phoneNumber)) {
                sharedPreferences.edit().putString(KEY_PHONE, phoneNumber).apply();
                Toast.makeText(getActivity(), "Emergency Contact Saved!", Toast.LENGTH_SHORT).show();

                // Clear EditText so it doesn't reflect previous number
                editTextPhone.setText("");

                // Go back to previous screen
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                Toast.makeText(getActivity(), "Enter a valid phone number!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
