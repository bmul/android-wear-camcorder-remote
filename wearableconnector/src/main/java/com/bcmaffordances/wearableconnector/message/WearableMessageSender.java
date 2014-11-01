package com.bcmaffordances.wearableconnector.message;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by bmullins on 10/31/14.
 */
public class WearableMessageSender extends Thread {

    private static final String TAG = "WearableMessageSender";

    private String mPath;
    private String mMessage;
    private GoogleApiClient mGoogleApiClient;

    // Constructor to send a message to the data layer
    public WearableMessageSender(String msg, GoogleApiClient googleApiClient) {
        mPath = "/camcorderRemote"; // specify endpoint at receiving node
        mMessage = msg;
        mGoogleApiClient = googleApiClient;
    }

    public void run() {
        // Broadcast message to call connected nodes
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : nodes.getNodes()) {
            MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                    mGoogleApiClient,
                    node.getId(),
                    mPath,
                    mMessage.getBytes()
            ).await();

            if (result.getStatus().isSuccess()) {
                Log.d(TAG, "Message: {" + mMessage + "} successfully sent to: " + node.getDisplayName());
            } else {
                Log.e(TAG, "Failed to send message to device");
            }
        }
    }
}
