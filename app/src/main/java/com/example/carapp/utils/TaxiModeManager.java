package com.example.carapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TaxiModeManager {

    private static final String PREF_NAME = "MyAppPrefs";
    private static final String TAXI_MODE = "taxi_mode";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static boolean getTaxiMode(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        // Retrieve the boolean value, defaulting to false if not found
        return sharedPreferences.getBoolean(TAXI_MODE, false);
    }

    public static void setTaxiMode(Context context, boolean newTaxiStatus) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(TAXI_MODE, newTaxiStatus);
        editor.apply();
    }
}


