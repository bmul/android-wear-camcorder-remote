package com.bcmaffordances.camcorderremote;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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

/**
 * Created by bmullins on 11/5/14.
 */
public class VideoCaptureActivity extends Activity {

    private static final String TAG = "VideoCaptureActivity";

    private Activity mActivity;
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

    @Override
    protected void onResume() {
        super.onResume();
        mVideoRecorder.init(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mOrientationListener.disable();
        mVideoRecorder.release();
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
            appRoot.removeView(mCameraPreviewFrame);
            LayoutInflater inflater = getLayoutInflater();
            inflater.inflate(R.layout.video_capture, appRoot);
        }
    }
}
