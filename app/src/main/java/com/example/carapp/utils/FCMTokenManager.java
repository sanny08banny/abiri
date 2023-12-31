package com.example.carapp.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.Loader;

import com.example.carapp.asynctasks.TokenIdLoader;
import com.google.firebase.messaging.FirebaseMessaging;

public class FCMTokenManager {
    private static final String PREF_FILE_NAME = "FCMTokenPrefs";
    private static final String PREF_TOKEN_KEY = "FCMTokenKey";

    public interface TokenCallback {
        void onTokenReceived(String token);
    }

    public static void fetchToken(TokenCallback callback) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String tokenId = task.getResult();
                        Log.e("Tag", "Token Id: " + tokenId);
                        callback.onTokenReceived(tokenId);
                    } else {
                        Log.e("Tag", "Error getting token: " + task.getException());
                        callback.onTokenReceived(null);
                    }
                });
    }

    public static void saveToken(Context context, String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_TOKEN_KEY, token);
        editor.apply();

        if (getCurrentAccountId(context) != null) {
            TokenIdLoader tokenIdLoader = new TokenIdLoader(context, token);
            tokenIdLoader.forceLoad();
            tokenIdLoader.registerListener(7, new Loader.OnLoadCompleteListener<String>() {
                @Override
                public void onLoadComplete(@NonNull Loader<String> loader, @Nullable String data) {
                    if (data != null) {

                    }
                }
            });
        }
    }
    public static String getCurrentAccountId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AccountPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("currentUserId", null);
    }
    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PREF_TOKEN_KEY, null);
    }
}
