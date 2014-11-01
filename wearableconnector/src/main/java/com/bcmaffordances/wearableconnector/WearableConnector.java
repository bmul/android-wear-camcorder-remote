package com.bcmaffordances.wearableconnector;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.bcmaffordances.wearableconnector.message.WearableMessageSender;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Wearable;


/**
 * Class to encapsulate connecting apps to wearable devices
 * and vice versa.
 */
public class WearableConnector {

    private static final String TAG = "WearableConnector";
    private GoogleApiClient mGoogleApiClient;

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
        if (mGoogleApiClient.isConnected()) {
            new WearableMessageSender(message, mGoogleApiClient).start();
        } else {
            Log.w(TAG, "Attempted to send message when not connected to Google API client.");
        }
    }

}
