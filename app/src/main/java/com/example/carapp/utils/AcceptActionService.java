package com.example.carapp.utils;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class AcceptActionService extends IntentService {
    private static final String TAG = "AcceptActionService";

    public AcceptActionService() {
        super("AcceptActionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals("ACCEPT_ACTION")) {
                // Handle accept action logic here
                Log.d(TAG, "Accept action clicked");
            }
        }
    }
}


