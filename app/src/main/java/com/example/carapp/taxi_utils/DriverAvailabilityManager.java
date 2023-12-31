package com.example.carapp.taxi_utils;

import android.content.Context;
import android.content.SharedPreferences;

public class DriverAvailabilityManager {

    private static final String PREF_NAME = "AvailabilityPrefs";
    private static final String KEY_STATUS = "availabilityStatus";
    private static final String KEY_SEAT_COUNT = "seatCount";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public DriverAvailabilityManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Save availability status
    public void saveAvailabilityStatus(boolean status) {
        editor.putBoolean(KEY_STATUS, status);
        editor.apply();
    }

    // Get availability status
    public boolean getAvailabilityStatus() {
        return sharedPreferences.getBoolean(KEY_STATUS, false);
    }

    // Save seat count
    public void saveSeatCount(int seatCount) {
        editor.putInt(KEY_SEAT_COUNT, seatCount);
        editor.apply();
    }

    // Get seat count
    public int getSeatCount() {
        return sharedPreferences.getInt(KEY_SEAT_COUNT, 0);
    }
}


