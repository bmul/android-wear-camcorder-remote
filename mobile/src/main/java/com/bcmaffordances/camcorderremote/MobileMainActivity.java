package com.bcmaffordances.camcorderremote;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.bcmaffordances.camcorderremote.com.bcmaffordances.camcorderremote.connection.WearableConnectionListener;
import com.bcmaffordances.camcorderremote.com.bcmaffordances.camcorderremote.connection.WearableConnector;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;

import java.util.Scanner;

/**
 * Mobile main activity
 */
public class MobileMainActivity extends Activity {

    private static final String TAG = "MobileMainActivity";
    private WearableConnector mWearableConnector;
    private MessageApi.MessageListener mMessageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_mobile_main);

        // TODO move to its own service class and use local broadcast manager?
        mMessageListener = new MessageApi.MessageListener() {
            @Override
            public void onMessageReceived (MessageEvent m){
                Log.d(TAG, "onMessageReceived: " + m.getPath());
                Scanner s = new Scanner(m.getPath());
                String command = s.next();
                if(command.equals("start")) {
                    Log.d(TAG, "Start message received");
                }
            }
        };

        mWearableConnector = new WearableConnector(this, new WearableConnectionListener() {
            @Override
            public void onConnected() {
                mWearableConnector.addListener(mMessageListener);
                String message = "Hello wearable!";
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
        mWearableConnector.removeListener(mMessageListener);
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
