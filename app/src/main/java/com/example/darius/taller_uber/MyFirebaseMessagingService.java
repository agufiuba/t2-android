package com.example.darius.taller_uber;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

// Como comunicar con la actividad: https://stackoverflow.com/questions/41925692/how-to-communicate-between-firebase-messaging-service-and-activity-android

enum FCM_MESSAGE_TYPE {FCM_DATA, FCM_MESSAGE, FCM_NOTIFICATION}

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    final String TAG = "FIREBASE_MSG_SERVICE";
    private LocalBroadcastManager broadcaster;

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        try {
            Log.d(TAG, remoteMessage.getData().toString());
            MessageRead messageRead = new MessageRead(remoteMessage);
            FCM_MESSAGE_TYPE message_type = messageRead.getMessage_type();
            putExtras(message_type.name(), messageRead);

        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void putExtras(String intentName, MessageRead messageRead) {
        Log.d(TAG, "Handling de mensaje de tipo " + messageRead.getMessage_type().name());
        Intent intent = new Intent(intentName);
        for (Map.Entry<String, String> entry : messageRead.getMap().entrySet()) {
            intent.putExtra(entry.getKey().substring(1), entry.getValue());
        }
        broadcaster.sendBroadcast(intent);
    }

    private class MessageRead {
        private FCM_MESSAGE_TYPE message_type;
        private Map<String, String> map = new HashMap<>();

        MessageRead(RemoteMessage remoteMessage) {
            map = remoteMessage.getData();
            if (map.containsKey("_passengerID") && map.containsKey("_from")) {
                this.message_type = FCM_MESSAGE_TYPE.FCM_DATA;
            } else {
                if (map.containsKey("_message")) {
                    this.message_type = FCM_MESSAGE_TYPE.FCM_MESSAGE;
                } else {
                    if (map.containsKey("_notification")) {
                        this.message_type = FCM_MESSAGE_TYPE.FCM_NOTIFICATION;
                    }
                }
            }
        }

        public FCM_MESSAGE_TYPE getMessage_type() {
            return this.message_type;
        }

        public Map<String, String> getMap() {
            return this.map;
        }
    }

    @Override
    public void onDeletedMessages() {
    }
}
