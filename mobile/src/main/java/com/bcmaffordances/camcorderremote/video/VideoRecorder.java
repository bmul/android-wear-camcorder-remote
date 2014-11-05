package com.bcmaffordances.camcorderremote.video;

import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;

import com.bcmaffordances.camcorderremote.stitching.VideoStitcher;

/**
 * This class provides video recording functionality.
 * It strives to encapsulate all the components/functionality that
 * Android requires for recording video so that clients can simply
 * record, pause, stop, etc.
 */
public class VideoRecorder {

    private static final String TAG = "VideoRecorder";
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private InitCameraListener mInitCameraListener;
    private ReleaseCameraListener mReleaseCameraListener;
    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private MediaRecorder mMediaRecorder;
    private Context mCtx;
    private VideoStitcher mVideoStitcher;
    private VideoFile mVideoFile;

    /**
     * Initialize the camera. This is done in the background since
     * it may take awhile.
     */
    public void init(Context context) {
        mCtx = context;
        new AsyncTask<Void, Void, Camera>() {
            @Override
            protected Camera doInBackground(Void... params) {
                try {
                    Camera cam = Camera.open();
                    return cam == null ? Camera.open(0) : cam;
                } catch (RuntimeException e) {
                    Log.wtf(TAG, "Failed to open camera", e);
                    return null;
                }
            }
            @Override
            protected void onPostExecute(Camera cam) {
                if (null != cam) {
                    setCameraObject(cam);
                    mCameraPreview = new CameraPreview(mCtx, cam);
                    if (null != mInitCameraListener) {
                        mInitCameraListener.onInit();
                    }
                } else {
                    Log.wtf(TAG, "Failed to init camera");
                }
            }
        }.execute();
    }

    public void release() {
        releaseMediaRecorder();
        releaseCameraObject();
        if (null != mReleaseCameraListener) {
            mReleaseCameraListener.onRelease();
        }
    }

    public void setOnInitCameraListener(InitCameraListener listener) {
        mInitCameraListener = listener;
    }

    public void setOnReleaseCameraListener(ReleaseCameraListener listener) {
        mReleaseCameraListener = listener;
    }

    public CameraPreview getCameraPreview() {
        return mCameraPreview;
    }

    public void startRecording() {
        Log.d(TAG, "startRecording()");
        record();
        mVideoStitcher = new VideoStitcher(mCtx, mVideoFile.getFile());
    }

    public void resumeRecording() {
        Log.d(TAG, "resumeRecording()");
        record();
        // Purposefully don't create a new mVideoStitcher object.
        // We need to keep it around so that we can continue
        // appending files.
    }

    public void pauseRecording() {
        Log.d(TAG, "pauseRecording()");
        stop();
        // Purposefully don't set the mVideoStitcher to null.
        // We need to keep it around so that we can continue
        // appending files.
    }

    public void stopRecording() {
        Log.d(TAG, "stopRecording()");
        stop();
        mVideoStitcher = null;
    }

    private void setCameraObject(Camera camera) {
        this.mCamera = camera;
    }

    private void releaseCameraObject() {
        if (null != mCamera) {
            mCamera.lock(); // unnecessary in API >= 14
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void releaseMediaRecorder() {
        if (null != mMediaRecorder) {
            mMediaRecorder.reset(); // clear configuration (optional here)
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    private void record() {

        mVideoFile = new VideoFile();
        // we need to unlock the camera so that mediaRecorder can use it
        mCamera.unlock(); // unnecessary in API >= 14
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        mMediaRecorder.setOutputFile(mVideoFile.getFile().getAbsolutePath());
        // int rotation = mCtx.getWindowManager().getDefaultDisplay().getRotation();
        //int orientation = ORIENTATIONS.get(rotation);
        //mMediaRecorder.setOrientationHint(orientation);
        mMediaRecorder.setPreviewDisplay(mCameraPreview.getHolder().getSurface());

        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start(); // throws IllegalStateException if not prepared
        } catch (Exception e) {
            Log.wtf(TAG, "Failed to prepare MediaRecorder", e);
            releaseMediaRecorder();
        }
    }

    private void stop() {
        try {
            mMediaRecorder.stop();
            mVideoStitcher.appendFile(mVideoFile.getFile());
            if (mVideoFile == null || !mVideoFile.getFile().exists()) {
                Log.w(TAG, "Video file does not exist after stop: " + mVideoFile.getFile().getAbsolutePath());
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to stop recording", e);
            mVideoFile.deleteFile();
        } finally {
            releaseMediaRecorder();
            mVideoFile = null;
        }
    }
}
