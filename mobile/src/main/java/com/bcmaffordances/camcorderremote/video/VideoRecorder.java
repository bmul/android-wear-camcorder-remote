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

    private CameraListener mCameraListener;
    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private MediaRecorder mMediaRecorder;
    private Context mCtx;
    private VideoStitcher mVideoStitcher;
    private VideoFile mVideoFile;
    private boolean mIsRecording = false;

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
                    if (null != mCameraListener) {
                        mCameraListener.onInit();
                    }
                } else {
                    Log.wtf(TAG, "Failed to init camera");
                }
            }
        }.execute();
    }

    /**
     * Release the camera.
     */
    public void release() {
        stop();
        releaseMediaRecorder();
        releaseCameraObject();
        if (null != mCameraListener) {
            mCameraListener.onRelease();
        }
    }

    /**
     * set a Camera event listener.
     * @param listener
     */
    public void setCameraListener(CameraListener listener) {
        mCameraListener = listener;
    }

    /**
     * Return the Camera preview.
     * @return CameraPreview object.
     */
    public CameraPreview getCameraPreview() {
        return mCameraPreview;
    }

    /**
     * Start recording a new video.
     * @throws VideoRecorderException if unable to record.
     */
    public void startRecording() throws VideoRecorderException {
        Log.d(TAG, "startRecording()");
        record();
        mVideoStitcher = new VideoStitcher(mCtx, mVideoFile.getFile());
    }

    /**
     * Resume recording a video.
     * @throws VideoRecorderException if unable to record.
     */
    public void resumeRecording() throws VideoRecorderException {
        Log.d(TAG, "resumeRecording()");
        record();
        // Purposefully don't create a new mVideoStitcher object.
        // We need to keep it around so that we can continue
        // appending files.
    }

    /**
     * Pause recording a video.
     */
    public void pauseRecording() {
        Log.d(TAG, "pauseRecording()");
        stop();
        // Purposefully don't set the mVideoStitcher to null.
        // We need to keep it around so that we can continue
        // appending files.
    }

    /**
     * Stop recording a video.
     */
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

    /**
     * Record video.
     * @throws VideoRecorderException if unable to record
     */
    private void record() throws VideoRecorderException {

        if (mIsRecording) {
            // already recording, so do nothing
            return;
        }

        try {
            mVideoFile = new VideoFile();
        } catch (VideoFileException e) {
            Log.e(TAG, "Failed to create a new VideoFile object");
            throw new VideoRecorderException("Failed to create a new VideoFile object");
        }

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
            mIsRecording = true;
        } catch (Exception e) {
            Log.wtf(TAG, "Failed to prepare MediaRecorder", e);
            releaseMediaRecorder();
        }
    }

    /**
     * Stop recording video.
     */
    private void stop() {

        if (!mIsRecording) {
            // already stopped, so do nothing
            return;
        }

        try {
            mMediaRecorder.stop();
            mIsRecording = false;
            mVideoStitcher.appendFile(mVideoFile.getFile());
        } catch (Exception e) {
            Log.e(TAG, "Failed to stop recording", e);
        } finally {
            releaseMediaRecorder();
            mVideoFile = null;
        }
    }
}
