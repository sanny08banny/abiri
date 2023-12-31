package com.example.carapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.example.carapp.R;
import com.example.carapp.databinding.ActivityLocationBinding;
import com.example.carapp.services.LocationWebSocketService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationActivity extends AppCompatActivity implements LocationWebSocketService.LocationUpdateListener{
    private static final String YOUR_API_KEY = "si7Azx54SdrUA3pXAGwzBShJv0ar7Bbw";
    private static final int REQUEST_CODE = 9;
    private ActivityLocationBinding locationBinding;
    private String Tag = "MapActivity";
    private LocationWebSocketService locationWebSocketService;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double currentLatitude;
    private double currentLongitude;

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop the WebSocket connection when the activity is destroyed
        if (locationWebSocketService != null) {
            locationWebSocketService.stopWebSocket();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationBinding = DataBindingUtil.setContentView(this, R.layout.activity_location);

        // Initialize the LocationWebSocketService
        locationWebSocketService = new LocationWebSocketService(this);
        locationWebSocketService.startWebSocket();

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    // Implement the LocationUpdateListener interface methods
    @Override
    public void onConnectionOpened() {
        // Handle WebSocket connection opened event
        getLocation();
    }

    @Override
    public void onLocationUpdateReceived(String updates) {
        // Handle received location updates
    }

    @Override
    public void onConnectionFailure(Throwable t) {
        // Handle WebSocket connection failure
    }

    @Override
    public void onConnectionClosed() {
        // Handle WebSocket connection closed event
    }
    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLatitude = location.getLatitude();
                            currentLongitude = location.getLongitude();

                        }
                    }
                });
    }

}