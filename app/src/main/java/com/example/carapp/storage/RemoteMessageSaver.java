package com.example.carapp.storage;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;

public class RemoteMessageSaver {

    private static final String PREF_NAME = "RemoteMessages";

    public static void saveRemoteMessage(Context context, RemoteMessage remoteMessage) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (remoteMessage != null && remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            // Convert remoteMessage data to a string or any suitable format and save it
            String messageData = convertDataToString(data);

            // Save the message data to SharedPreferences
            editor.putString("lastMessage", messageData);
            editor.apply();
        }
    }

    private static String convertDataToString(Map<String, String> data) {
        // Convert Map data to a suitable string format for saving
        // Example: Convert to JSON or any format that preserves the data
        // For demonstration, assume converting to JSON
        // (You may use libraries like Gson for better JSON handling)
        // Sample implementation:
        // return new JSONObject(data).toString();
        // For simplicity, just concatenating key-value pairs for demonstration purposes
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            stringBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return stringBuilder.toString();
    }

    public static String retrieveLastSavedMessage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("lastMessage", "");
    }
}

