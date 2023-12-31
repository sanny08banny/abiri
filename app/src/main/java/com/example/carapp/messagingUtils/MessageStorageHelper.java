package com.example.carapp.messagingUtils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MessageStorageHelper {
    private static final String PREF_NAME = "MessageCache";

    // Method to save a list of ChatMessage objects
    public static void cacheMessagesLocally(Context context, List<ChatMessage> messages) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Serialize the list of ChatMessage objects to JSON using Gson
        Gson gson = new Gson();
        String messagesJson = gson.toJson(messages);

        // Save the JSON string in SharedPreferences
        editor.putString("cachedMessages", messagesJson);
        editor.apply();
    }

    // Method to retrieve cached messages as a list of ChatMessage objects
    public static List<ChatMessage> getCachedMessages(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String messagesJson = sharedPreferences.getString("cachedMessages", null);

        if (messagesJson != null) {
            // Deserialize the JSON string to a list of ChatMessage objects using Gson
            Gson gson = new Gson();
            Type type = new TypeToken<List<ChatMessage>>() {}.getType();
            return gson.fromJson(messagesJson, type);
        } else {
            return new ArrayList<>(); // Return an empty list if no messages are cached
        }
    }

    // Method to delete a specific chat message by its position in the list
    public static void deleteMessage(Context context, int position) {
        List<ChatMessage> messages = getCachedMessages(context);

        if (position >= 0 && position < messages.size()) {
            messages.remove(position); // Remove the message at the specified position
            cacheMessagesLocally(context, messages); // Save the updated list
        }
    }
}

