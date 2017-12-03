package com.example.darius.taller_uber;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.content.ContentValues.TAG;

// Como comunicar con la actividad: https://stackoverflow.com/questions/41925692/how-to-communicate-between-firebase-messaging-service-and-activity-android

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    final String TAG = "FIREBASE_MSG_SERVICE";
    private LocalBroadcastManager broadcaster;


    public MyFirebaseMessagingService() {
    }

    @Override
    public void onCreate(){
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
        try{
            Log.d(TAG, remoteMessage.getData().toString());
            Intent intent = new Intent("FCM_Message");
            intent.putExtra("passengerID", remoteMessage.getData().get("passengerID"));
            intent.putExtra("from", remoteMessage.getData().get("_from"));
            intent.putExtra("to", remoteMessage.getData().get("_to"));
            intent.putExtra("name", remoteMessage.getData().get("name"));
            intent.putExtra("last_name", remoteMessage.getData().get("last_name"));

            broadcaster.sendBroadcast(intent);
        } catch (NullPointerException e){
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onDeletedMessages() {
    }
}
