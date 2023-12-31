package com.example.carapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.carapp.entities.Car;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class DataCache {
    private static final String PREF_KEY = "cached_data";
    private static final String LAST_UPDATE_KEY = "last_update_time";

    public static void saveData(Context context, List<Car> data) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        // Save the current timestamp as the last update time
        long currentTime = System.currentTimeMillis();
        editor.putLong(LAST_UPDATE_KEY, currentTime);

        String jsonData = gson.toJson(data);
        editor.putString("data", jsonData);
        editor.apply();
    }

    public static List<Car> loadData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonData = sharedPreferences.getString("data", null);
        Type type = new TypeToken<List<Car>>() {}.getType();
        return gson.fromJson(jsonData, type);
    }

    public static long getLastUpdateTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(LAST_UPDATE_KEY, 0);
    }
}


