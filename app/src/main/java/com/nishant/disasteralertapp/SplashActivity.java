package com.nishant.disasteralertapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_TIME_OUT = 3500; // 3 seconds delay
    private TextView txtTitle;
    private String titleText = "GeoPulse";  // Full text
    private int index = 0;
    private Handler handler = new Handler();
    private SpannableStringBuilder displayedText = new SpannableStringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.orange));

        // Find views
        txtTitle = findViewById(R.id.txtTitle);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        // Make the TextView visible
        txtTitle.setVisibility(TextView.VISIBLE);

        // Start the letter-by-letter animation
        animateTitle();

        // Move to Home Screen after SPLASH_TIME_OUT
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_TIME_OUT);
    }

    private void animateTitle() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (index < titleText.length()) {
                    displayedText.append(titleText.charAt(index));  // Append next letter
                    txtTitle.setText(displayedText);  // Update the TextView
                    index++;
                    handler.postDelayed(this, 200); // Adjust speed (200ms per letter)
                }
            }
        }, 200);
    }
}
