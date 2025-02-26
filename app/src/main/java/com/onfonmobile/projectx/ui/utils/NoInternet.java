package com.onfonmobile.projectx.ui.utils;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.onfonmobile.projectx.Firestore.Helpers.Utils.NetworkUtils;
import com.onfonmobile.projectx.R;
import com.onfonmobile.projectx.ui.activities.LoginActivity;

public class NoInternet extends AppCompatActivity {
    private MaterialButton retryButton;
    private MaterialButton openSettingsButton;
    private TextView offlineModeText;
    private ImageView noInternetImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        // Initialize views
        retryButton = findViewById(R.id.retryButton);
        openSettingsButton = findViewById(R.id.openSettingsButton);
        offlineModeText = findViewById(R.id.offlineModeText);
        noInternetImage = findViewById(R.id.noInternetImage);

        // Load GIF using Glide
        Glide.with(this).asGif().load(R.drawable.wifi).into(noInternetImage);

        // Set up retry button click listener
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isOnline(NoInternet.this)) {
                    startActivity(new Intent(NoInternet.this, LoginActivity.class));
                    finish(); // Close NoInternetActivity
                } else {
                    // Show Snackbar instead of Toast
                    Snackbar.make(v, "Still no internet. Please check your connection.", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        // Set up settings button click listener
        openSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open device's wireless settings
                Intent settingsIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(settingsIntent);
            }
        });

        // Set up offline mode text click listener
        offlineModeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // You can implement your offline mode logic here
                // For example, start a specific offline activity or enable offline mode
                Snackbar.make(v, "Continuing in offline mode...", Snackbar.LENGTH_SHORT).show();

                // Example: Start an offline activity or main activity with offline flag
                Intent offlineIntent = new Intent(NoInternet.this, LoginActivity.class);
                offlineIntent.putExtra("OFFLINE_MODE", true);
                startActivity(offlineIntent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check connection when returning from settings
        if (NetworkUtils.isOnline(this)) {
            startActivity(new Intent(NoInternet.this, LoginActivity.class));
            finish();
        }
    }
}