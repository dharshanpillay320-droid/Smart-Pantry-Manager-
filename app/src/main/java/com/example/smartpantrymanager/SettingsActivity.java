package com.example.smartpantrymanager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Configuration screen for app-wide settings (dark mode, expiry alerts).
// Launched from the MainActivity.
// On finish, it returns the user directly back to the MainActivity.
public class SettingsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Switch switchDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Setup screen layout
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Link UI elements
        btnBack = findViewById(R.id.btnBack);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        Switch switchExpiryAlerts = findViewById(R.id.switchExpiryAlerts);

        // Load current dark mode state
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switchDarkMode.setChecked(currentNightMode == Configuration.UI_MODE_NIGHT_YES);

        // Load current expiry alerts state
        android.content.SharedPreferences prefs = getSharedPreferences("SmartPantryPrefs", android.content.Context.MODE_PRIVATE);
        boolean isAlertsEnabled = prefs.getBoolean("enable_expiry_alerts", true);
        switchExpiryAlerts.setChecked(isAlertsEnabled);

        // Handle back navigation
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Update application theme on switch change
        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });

        // Update expiry alerts preference on switch change
        switchExpiryAlerts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                android.content.SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("enable_expiry_alerts", isChecked);
                editor.apply();
            }
        });
    }
}
