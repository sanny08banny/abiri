package com.example.carapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREF_NAME = "TokenManagerPrefs";
    private static final String KEY_TOKEN_AMOUNT = "tokenAmount";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public TokenManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setTokenAmount(double amount) {
        editor.putFloat(KEY_TOKEN_AMOUNT, (float) amount);
        editor.apply();
    }

    public double getTokenAmount() {
        return preferences.getFloat(KEY_TOKEN_AMOUNT, 0.0f);
    }
}
