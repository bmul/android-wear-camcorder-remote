package com.bcmaffordances.wearableconnector;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.bcmaffordances.wearableconnector.message.WearableMessageSender;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.Node;


/**
 * Class to encapsulate connecting to wearable devices.
 */
public class WearableConnector {

    private static final String TAG = "WearableConnector";
    private GoogleApiClient mGoogleApiClient;
    private Node mWearableNode = null;

    public WearableConnector(Context ctx, final WearableConnectionListener listener) {

        mGoogleApiClient = new GoogleApiClient.Builder(ctx)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        //findWearableNode(); TODO remove
                        if (null != listener) {
                            listener.onConnected();
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();
    }

    public void connect() {
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    public void disconnect() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void addListener(MessageApi.MessageListener listener) {
        if (mGoogleApiClient.isConnected()) {
            Wearable.MessageApi.addListener(mGoogleApiClient, listener);
        }
    }

    public void removeListener(MessageApi.MessageListener listener) {
        if (mGoogleApiClient.isConnected()) {
            Wearable.MessageApi.removeListener(mGoogleApiClient, listener);
        }
    }

    public void sendMessage(String message) {
        new WearableMessageSender(message, mGoogleApiClient).start();
    }

    // Alternative approach. Had a problem with trying to send messages before the
    // nodes were connected.
    public void sendMessageToWearable(String path, byte[] data, final ResultCallback<MessageApi.SendMessageResult> callback) {
        if (mGoogleApiClient.isConnected() && mWearableNode != null) {
            PendingResult<MessageApi.SendMessageResult> pending = Wearable.MessageApi.sendMessage(mGoogleApiClient, mWearableNode.getId(), path, data);
            pending.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                @Override
                public void onResult(MessageApi.SendMessageResult result) {
                    if (callback != null) {
                        callback.onResult(result);
                    }
                    if (!result.getStatus().isSuccess()) {
                        Log.e(TAG, "Failed to send message to wearable: " + result.getStatus());
                    } else {
                        Log.d(TAG, "Message sent successfully");
                    }
                }
            });
        } else {
            Log.e(TAG, "Attempted to send message before wearable device was found");
        }
    }

    /**
     * Finds a single wearable device.
     */
    private void findWearableNode() {
        if (mGoogleApiClient.isConnected()) {
            PendingResult<NodeApi.GetConnectedNodesResult> nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
            nodes.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                @Override
                public void onResult(NodeApi.GetConnectedNodesResult result) {
                    if(result.getNodes().size()>0) {
                        mWearableNode = result.getNodes().get(0);
                        Log.d(TAG, "Found wearable: name=" + mWearableNode.getDisplayName() + ", id=" + mWearableNode.getId());
                    } else {
                        mWearableNode = null;
                    }
                }
            });
        }
    }

}
