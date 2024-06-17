package com.example.medicinetracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Setting extends AppCompatActivity {
    private static final int REQUEST_CALL_PERMISSION = 1;
    private static final String TAG = "SettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        Button backButton = findViewById(R.id.backButton);
        Switch emergencyCallSwitch = findViewById(R.id.emergencyCallSwitch);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(Setting.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        emergencyCallSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (ContextCompat.checkSelfPermission(Setting.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Requesting CALL_PHONE permission");
                    ActivityCompat.requestPermissions(Setting.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
                } else {
                    Log.d(TAG, "CALL_PHONE permission already granted");
                    makeEmergencyCall();
                }
            }
        });
    }

    private void makeEmergencyCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:09364663229"));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Making the emergency call");
            startActivity(callIntent);
        } else {
            Log.d(TAG, "Call permission not granted");
            Toast.makeText(this, "Call permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "CALL_PHONE permission granted");
                makeEmergencyCall();
            } else {
                Log.d(TAG, "CALL_PHONE permission denied");
                Toast.makeText(this, "Call permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void records(View view) {
        Intent intent = new Intent(Setting.this, recordshistory.class);
        startActivity(intent);
    }
}
