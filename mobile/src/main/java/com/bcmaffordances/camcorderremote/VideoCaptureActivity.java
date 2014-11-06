package com.bcmaffordances.camcorderremote;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.bcmaffordances.camcorderremote.state.PausedRecordingState;
import com.bcmaffordances.camcorderremote.state.ReadyRecordingState;
import com.bcmaffordances.camcorderremote.state.RecordingStateContext;
import com.bcmaffordances.camcorderremote.state.ResumedRecordingState;
import com.bcmaffordances.camcorderremote.state.StartedRecordingState;
import com.bcmaffordances.camcorderremote.state.StoppedRecordingState;
import com.bcmaffordances.camcorderremote.video.InitCameraListener;
import com.bcmaffordances.camcorderremote.video.ReleaseCameraListener;
import com.bcmaffordances.camcorderremote.video.VideoRecorder;
import com.bcmaffordances.wearableconnector.CamcorderRemoteConstants;
import com.bcmaffordances.wearableconnector.WearableConnector;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by bmullins on 11/5/14.
 */
public class VideoCaptureActivity extends Activity {

    private static final String TAG = "VideoCaptureActivity";

    private Activity mActivity;

    private WearableConnector mWearableConnector;
    private BroadcastReceiver mLocalMessageReceiver;

    private VideoRecorder mVideoRecorder;
    private RecordingStateContext mRecordingStateContext;
    private FrameLayout mCameraPreviewFrame;
    private VideoCaptureOrientationEventListener mOrientationListener;
    // The degrees of the device rotated clockwise from its natural orientation.
    private int mLastRawOrientation = OrientationEventListener.ORIENTATION_UNKNOWN;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.setContentView(R.layout.video_capture);
        mCameraPreviewFrame = (FrameLayout)super.findViewById(R.id.camera_preview);

        mActivity = this;
        initWearableConnector();
        initVideoRecorder();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWearableConnector.connect();
        mVideoRecorder.init(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mOrientationListener.disable();
        mVideoRecorder.release();
    }

    @Override
    public void onStop() {
        super.onStop();
        mWearableConnector.disconnect();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalMessageReceiver);
        super.onDestroy();
    }

    // gets called by the button press
    public void startRecording(View v) {
        Log.d(TAG, "startRecording()");
        mOrientationListener.enable();
        mVideoRecorder.startRecording();
        mRecordingStateContext.changeState(new StartedRecordingState(mActivity));
        mRecordingStateContext.updateDisplayedButtons();
    }

    // gets called by the button press
    public void resumeRecording(View v) {
        Log.d(TAG, "resumeRecording()");
        mVideoRecorder.resumeRecording();
        mRecordingStateContext.changeState(new ResumedRecordingState(mActivity));
        mRecordingStateContext.updateDisplayedButtons();
    }

    // gets called by the button press
    public void pauseRecording(View v) {
        Log.d(TAG, "pauseRecording()");
        mVideoRecorder.pauseRecording();
        mRecordingStateContext.changeState(new PausedRecordingState(mActivity));
        mRecordingStateContext.updateDisplayedButtons();
    }

    // gets called by the button press
    public void stopRecording(View v) {
        Log.d(TAG, "stopRecording()");
        mVideoRecorder.stopRecording();
        mRecordingStateContext.changeState(new StoppedRecordingState(mActivity));
        mRecordingStateContext.updateDisplayedButtons();
    }

    private class VideoCaptureOrientationEventListener extends OrientationEventListener {

        public VideoCaptureOrientationEventListener(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {

            // TODO This isn't being invoked ever. In v.next, it would be
            // cool to just rotate the camera button images on rotation events.

            // Keep the last known orientation. If the user first orients
            // the camera and then points the camera to floor or sky, we still have
            // the correct orientation.
            Log.d(TAG, "onOrientationChanged()");
            if (orientation == ORIENTATION_UNKNOWN) return;

            mLastRawOrientation = orientation;

            // create new layout with the current orientation
            Log.d(TAG, "Redrawing view due to orientation change");
            ViewGroup appRoot = (ViewGroup) findViewById(R.id.preview_root);
            //appRoot.removeView(mCameraPreviewFrame);
            LayoutInflater inflater = getLayoutInflater();
            //inflater.inflate(R.layout.video_capture, appRoot);
        }
    }

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

    private void initVideoRecorder() {

        mOrientationListener = new VideoCaptureOrientationEventListener(this);

        mRecordingStateContext = new RecordingStateContext(mActivity);
        mRecordingStateContext.updateDisplayedButtons();

        mVideoRecorder = new VideoRecorder();
        mVideoRecorder.setOnInitCameraListener(new InitCameraListener(){
            @Override
            public void onInit() {
                mCameraPreviewFrame.addView(mVideoRecorder.getCameraPreview(), 0);
                mRecordingStateContext.changeState(new ReadyRecordingState(mActivity));
                mRecordingStateContext.updateDisplayedButtons();
            }
        });
        mVideoRecorder.setOnReleaseCameraListener(new ReleaseCameraListener(){
            @Override
            public void onRelease() {
                mCameraPreviewFrame.removeView(mVideoRecorder.getCameraPreview());
            }
        });
    }
}
