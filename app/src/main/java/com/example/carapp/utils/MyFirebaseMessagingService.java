package com.example.carapp.utils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.example.carapp.R;
import com.example.carapp.activities.MainActivity;
import com.example.carapp.storage.RemoteMessageSaver;
import com.example.carapp.taxi_utils.ClientRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "abiri_message";
    private static final int NOTIFICATION_ID = 123;

    @Override
    public void onNewToken(@NonNull String token) {
        FCMTokenManager.saveToken(getApplicationContext(), token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        createNotificationChannel();
        saveMessage(remoteMessage);
        if (remoteMessage.getData().size() > 0) {
            Log.e("Messaging", "isReceived");
            Log.e("Messaging", String.valueOf(remoteMessage.getData()));

            RemoteMessage.Notification notification = remoteMessage.getNotification();

            Map<String, String> data = remoteMessage.getData();

            assert notification != null;
            String content = notification.getBody();
            assert content != null;
            Log.d("Messaging", content);

            String title = notification.getTitle();
            String ride_id = data.get("ride_id");
            String user_name = data.get("user_name");
            String user_phone = data.get("user_phone");
            String client_id = data.get("client_id");
            float dest_lat = Float.parseFloat(data.get("dest_lat"));
            float dest_lon = Float.parseFloat(data.get("dest_lon"));
            float current_lat = Float.parseFloat(data.get("current_lat"));
            float current_lon = Float.parseFloat(data.get("current_lon"));

            ClientRequest request = new ClientRequest();
            request.setRide_id(ride_id);
            request.setClient_id(client_id);
            request.setUser_name(user_name);
            request.setUser_phone(user_phone);
            request.setCurrent_lat(current_lat);
            request.setCurrent_lon(current_lon);
            request.setDest_lat(dest_lat);
            request.setDest_lon(dest_lon);


            Log.d("Messaging", ride_id + user_name);

            // Handle the data payload and notification payload based on your requirements
            showNotification(content, request,title);
        }
        super.onMessageReceived(remoteMessage);
    }

    private void saveMessage(RemoteMessage remoteMessage) {
        RemoteMessageSaver.saveRemoteMessage(getApplicationContext(),remoteMessage);
    }

    private void showNotification(String content,
                                  ClientRequest clientRequest, String title) {
        // Create a notification channel (for Android 8.0 and above)

        // Load the large icon and image using Glide
        Bitmap largeIconBitmap = null;
            try {
                largeIconBitmap = Glide.with(this)
                        .asBitmap()
                        .load(R.mipmap.ic_abiri)
                        .submit()
                        .get();
            } catch (Exception e) {
                e.printStackTrace();
            }

        // Create the notification intent
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("request", clientRequest);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent acceptIntent = new Intent(this, MainActivity.class);
        acceptIntent.putExtra("request", clientRequest);
        acceptIntent.setAction("ACCEPT_ACTION");
// Add any necessary extras to the acceptIntent if required
        PendingIntent acceptPendingIntent = PendingIntent.getActivity(this,
                0, acceptIntent,  PendingIntent.FLAG_IMMUTABLE);

        Intent declineIntent = new Intent(this, MainActivity.class);
        declineIntent.putExtra("request", clientRequest);
        declineIntent.setAction("DECLINE_ACTION");
// Add any necessary extras to the declineIntent if required
        PendingIntent declinePendingIntent = PendingIntent.getActivity(this, 0,
                declineIntent,  PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_abiri_foreground)
                .setLargeIcon(largeIconBitmap)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                // Add accept and decline actions
                .addAction(R.drawable.baseline_check_24, "Accept", acceptPendingIntent)
                .addAction(R.drawable.baseline_error_outline_24, "Decline", declinePendingIntent);


        // Display the notification
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "My Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Description");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

