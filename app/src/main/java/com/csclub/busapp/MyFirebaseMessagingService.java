package com.csclub.busapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID_1 = "Status Change";
    private static final String CHANNEL_ID_2 = "Bus Change";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            SharedPreferences localPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            String userBusNumber = localPrefs.getString("userBusNumber", "-1");

            boolean reset = true;
            for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                if (!(entry.getValue().equals("NOT HERE")) || entry.getKey().contains("=")) {
                    reset = false;
                }
            }

            if (reset) {
                localPrefs.edit().putString("CHANGES", "-1").apply();
                localPrefs.edit().putString("HERE", "-1").apply();
                localPrefs.edit().putString("LOADING", "-1").apply();
                localPrefs.edit().putString("GONE", "-1").apply();
            }

            for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (!(value.equals("NOT HERE"))) {
                    if (key.contains(userBusNumber)) {
                        String s = localPrefs.getString(value, "-1");
                        if (!(s.contains("Bus " + userBusNumber) && s.contains(value))) {
                            String text = "";
                            if (key.contains("=")) {
                                String busChange = key.substring(key.indexOf("=") + 1);
                                text = "Bus " + userBusNumber + " (" + busChange + ")" + " is " + value;
                            } else {
                                text = "Bus " + userBusNumber + " is " + value;
                            }
                            sendNotification("Status Change", text, CHANNEL_ID_1, 0);
                            localPrefs.edit().putString(value, text).apply();
                        }

                        if (key.contains("=")) {
                            String busChange = key.substring(key.indexOf("=") + 1);
                            if (!(busChange.equals(localPrefs.getString("CHANGES", "-1")))) {
                                String text = "Bus " + userBusNumber + " is now Bus " + busChange;
                                sendNotification("Bus Change", text, CHANNEL_ID_2, 1);
                                localPrefs.edit().putString("CHANGES", busChange).apply();
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    public void sendNotification(String name, String text, String channelID, int id) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID);
        builder.setContentTitle(name)
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        R.mipmap.ic_launcher))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[] { 0, 300, 200, 300 })
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }
}