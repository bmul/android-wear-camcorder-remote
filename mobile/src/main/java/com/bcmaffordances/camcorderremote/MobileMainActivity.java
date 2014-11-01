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

import com.bcmaffordances.wearableconnector.WearableConnectionListener;
import com.bcmaffordances.wearableconnector.WearableConnector;
import com.bcmaffordances.wearableconnector.message.WearableMessageListenerService;

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

        mWearableConnector = new WearableConnector(this, new WearableConnectionListener() {
            @Override
            public void onConnected() {
                String message = "Hello wearable!";
                mWearableConnector.sendMessage(message);
            }
        });

        // Register a local broadcast receiver to handle messages from wearable
        // devices that have been received by the message listening service.
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        mLocalMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra(WearableMessageListenerService.MESSAGE_INTENT_EXTRA);
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
