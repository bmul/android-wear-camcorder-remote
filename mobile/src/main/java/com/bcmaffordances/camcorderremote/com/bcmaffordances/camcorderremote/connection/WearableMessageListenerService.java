package com.bcmaffordances.camcorderremote.com.bcmaffordances.camcorderremote.connection;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Mobile app message listening service.
 */
public class WearableMessageListenerService extends WearableListenerService {

    private static final String TAG = "MobileWearableMessageListenerService";
    private static final String MESSAGE_PATH = "/camcorderRemote";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.d(TAG, "Incoming message on mobile app...");
        if (messageEvent.getPath().equals(MESSAGE_PATH)) {
            final String message = new String(messageEvent.getData());
            Log.d(TAG, "Message path received on app is: " + messageEvent.getPath());
            Log.d(TAG, "Message received on app is: " + message);
        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}
