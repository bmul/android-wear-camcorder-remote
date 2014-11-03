package com.bcmaffordances.camcorderremote;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.bcmaffordances.wearableconnector.CamcorderRemoteConstants;
import com.bcmaffordances.wearableconnector.WearableConnector;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Mobile main activity
 */
public class MobileMainActivity extends Activity {

    private static final String TAG = "MobileMainActivity";
    private WearableConnector mWearableConnector;
    private BroadcastReceiver mLocalMessageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_mobile_main);

        // Setup wearable connection callbacks
        GoogleApiClient.OnConnectionFailedListener wearableConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult result) {
                Log.d(TAG, "onConnectionFailed: " + result);
            }
        };
        GoogleApiClient.ConnectionCallbacks wearableConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle connectionHint) {
                Log.d(TAG, "onConnected: " + connectionHint);
                String message = "Hello wearable!";
                mWearableConnector.sendMessage(message);
            }
            @Override
            public void onConnectionSuspended(int cause) {
                Log.d(TAG, "onConnectionSuspended: " + cause);
            }
        };

        mWearableConnector = new WearableConnector(
                this,
                wearableConnectionCallbacks,
                wearableConnectionFailedListener);

        // Register a local broadcast receiver to handle messages that
        // have been received by the wearable message listening service.
        mLocalMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra(CamcorderRemoteConstants.MESSAGE_INTENT_EXTRA);
                Log.d(TAG, "Message received from wearable: " + message);
                if (message.equals(CamcorderRemoteConstants.REQUEST_RECORD)) {
                    mWearableConnector.sendMessage(CamcorderRemoteConstants.RESPONSE_RECORDING);
                }
                else if(message.equals(CamcorderRemoteConstants.REQUEST_RESUME)) {
                    mWearableConnector.sendMessage(CamcorderRemoteConstants.RESPONSE_RESUMED);
                }
                else if(message.equals(CamcorderRemoteConstants.REQUEST_PAUSE)) {
                    mWearableConnector.sendMessage(CamcorderRemoteConstants.RESPONSE_PAUSED);
                }
                else if(message.equals(CamcorderRemoteConstants.REQUEST_STOP)) {
                    mWearableConnector.sendMessage(CamcorderRemoteConstants.RESPONSE_STOPPED);
                }

            }
        };
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalMessageReceiver, messageFilter);

    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        mWearableConnector.connect();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        mWearableConnector.disconnect();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalMessageReceiver);
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mobile_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
