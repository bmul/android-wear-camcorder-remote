package com.bcmaffordances.camcorderremote;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.view.WindowManager;

import com.bcmaffordances.camcorderremote.state.ReadyState;
import com.bcmaffordances.camcorderremote.state.WearableRecordingStateContext;
import com.bcmaffordances.camcorderremotecommon.CamcorderRemoteConstants;
import com.bcmaffordances.wearableconnector.WearableConnector;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Main activity for the wearable device.
 */
public class WearMainActivity extends Activity {

    private static final String TAG = "WearMainActivity";

    private GridViewPager mGridViewPager;
    private CamcorderRemotePagerAdapter mCamcorderRemotePagerAdapter;
    private WearableConnector mWearableConnector;
    private BroadcastReceiver mLocalMessageReceiver;
    private WearableRecordingStateContext mRecordingStateContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear_main);
        initWearableConnector();
        mRecordingStateContext = new WearableRecordingStateContext(mWearableConnector);
        mCamcorderRemotePagerAdapter = new CamcorderRemotePagerAdapter(getFragmentManager(), mRecordingStateContext);
        mGridViewPager = (GridViewPager) findViewById(R.id.pager);
        mGridViewPager.setAdapter(mCamcorderRemotePagerAdapter);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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

    /**
     * Initialize the wearable connector which will
     * facilitate communication to mobile devices.
     */
    private void initWearableConnector() {

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
                Log.d(TAG, "onConnected: ");
                mRecordingStateContext.changeState(new ReadyState());
                mCamcorderRemotePagerAdapter.notifyDataSetChanged();
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
                Log.d(TAG, "Message received from app: " + message);
                // Update UI and onClick behavior based upon the recording state received from the mobile app
                mRecordingStateContext.changeState(message);
                mCamcorderRemotePagerAdapter.notifyDataSetChanged(); // refresh UI
            }
        };
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalMessageReceiver, messageFilter);

    }
}
