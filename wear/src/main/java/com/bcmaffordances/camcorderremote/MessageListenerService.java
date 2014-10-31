package com.bcmaffordances.camcorderremote;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Wearable Message listening service.
 */
public class MessageListenerService extends WearableListenerService {

    private static final String TAG = "MessageListenerService";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.d(TAG, "Incoming message");
        if (messageEvent.getPath().equals("/camcorderRemote")) {
            final String message = new String(messageEvent.getData());
            Log.d(TAG, "Message path received on watch is: " + messageEvent.getPath());
            Log.d(TAG, "Message received on watch is: " + message);
        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}
