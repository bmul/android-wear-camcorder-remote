package com.bcmaffordances.wearableconnector.message;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by bmullins on 10/31/14.
 */
public class WearableMessageListenerService extends WearableListenerService {

    private static final String TAG = "WearableMessageListenerService";

    public static final String MESSAGE_PATH = "/camcorderRemote";
    public static final String MESSAGE_INTENT_EXTRA = "message";


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.d(TAG, "Incoming message...");

        if (messageEvent.getPath().equals(MESSAGE_PATH)) {
            final String message = new String(messageEvent.getData());
            Log.d(TAG, "Message path: " + messageEvent.getPath());
            Log.d(TAG, "Message received: " + message);

            // Broadcast message to activity for handling
            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra(MESSAGE_INTENT_EXTRA, message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}
