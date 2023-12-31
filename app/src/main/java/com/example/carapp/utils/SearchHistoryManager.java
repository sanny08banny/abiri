package com.example.carapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchHistoryManager {
    private static final String PREF_NAME = "SearchHistory";
    private static final String KEY_HISTORY = "search_history";

    public static void addQuery(Context context, String query) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> history = preferences.getStringSet(KEY_HISTORY, new HashSet<>());
        history.add(query);
        preferences.edit().putStringSet(KEY_HISTORY, history).apply();
    }

    public static List<String> getSearchHistory(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> history = preferences.getStringSet(KEY_HISTORY, new HashSet<>());
        return new ArrayList<>(history);
    }

    public static List<String> getSearchHistoryByQuery(Context context, String query) {
        List<String> history = getSearchHistory(context);
        List<String> filteredHistory = new ArrayList<>();
        for (String item : history) {
            if (item.contains(query)) {
                filteredHistory.add(item);
            }
        }
        return filteredHistory;
    }

    public static void deleteQuery(Context context, String query) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> history = preferences.getStringSet(KEY_HISTORY, new HashSet<>());

        // Remove the query from the set
        history.remove(query);

        // Store the updated set back in SharedPreferences
        preferences.edit().putStringSet(KEY_HISTORY, history).apply();
    }


    public static void clearSearchHistory(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().remove(KEY_HISTORY).apply();
    }
}


