package com.bcmaffordances.camcorderremote;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.bcmaffordances.wearableconnector.WearableConnectionListener;
import com.bcmaffordances.wearableconnector.WearableConnector;

public class WearMainActivity extends Activity {

    private static final String TAG = "WearMainActivity";

    private TextView mTextView;
    private WearableConnector mWearableConnector;

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
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalMessageReceiver);
        super.onDestroy();
    }
}
