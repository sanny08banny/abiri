package com.example.carapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class IpAddressManager {

    private static final String PREF_NAME = "MyAppPrefs";
    private static final String KEY_IP_ADDRESS = "ip_address";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static String getIpAddress(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(KEY_IP_ADDRESS, getDefaultIpAddress());
    }

    public static void setIpAddress(Context context, String newIpAddress) {
        String newBaseUrl = "http://" + newIpAddress + ":4000";
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_IP_ADDRESS, newBaseUrl);
        editor.apply();
    }

    private static String getDefaultIpAddress() {
        // Set your default IP address here
        return "http://100.24.52.107:4000";
    }
}

