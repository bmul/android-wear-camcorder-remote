package com.bcmaffordances.camcorderremote;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.bcmaffordances.wearableconnector.WearableConnectionListener;
import com.bcmaffordances.wearableconnector.WearableConnector;
import com.bcmaffordances.wearableconnector.WearableConnectorConstants;

public class WearMainActivity extends Activity {

    private static final String TAG = "WearMainActivity";

    private TextView mTextView;
    private WearableConnector mWearableConnector;
    private BroadcastReceiver mLocalMessageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear_main);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });

        mWearableConnector = new WearableConnector(this, new WearableConnectionListener() {
            @Override
            public void onConnected() {
                String message = "Hello app from wearable!";
                mWearableConnector.sendMessage(message);
            }
        });

        // Register a local broadcast receiver to handle messages from handheld
        // devices that have been received by the message listening service.
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        mLocalMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra(WearableConnectorConstants.MESSAGE_INTENT_EXTRA);
                Log.d(TAG, "Message received: " + message);
            }
        };
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
}
