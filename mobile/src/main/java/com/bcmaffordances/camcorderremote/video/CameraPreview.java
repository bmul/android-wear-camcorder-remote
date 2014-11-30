package com.bcmaffordances.camcorderremote.video;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Class to encapsulate the functionality required to generate
 * a preview of what the camera is seeing.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "CameraPreview";

    private final Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        super.getHolder().addCallback(this);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated()");
        try {
            this.mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            Log.wtf(TAG, "Failed to start camera preview", e);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed()");
        //mCamera.stopPreview();  TODO this was causing crashes
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        Log.d(TAG, "surfaceChanged()");

        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        if (!previewSizes.isEmpty()) {
            Camera.Size largestPreviewSize = previewSizes.get(0);
            parameters.setPreviewSize(largestPreviewSize.width, largestPreviewSize.height);
        } else {
            Log.e(TAG, "Camera preview sizes are unavailable");
        }
        requestLayout();

        // The last fps range item will have the greatest max fps.
        List<int[]> supportedFpsRange = parameters.getSupportedPreviewFpsRange();
        int[] fpsRange = supportedFpsRange.get(supportedFpsRange.size() - 1);
        parameters.setPreviewFpsRange(
                fpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
                fpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
        Log.d(TAG, "Frame rates: "
                + fpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX]
                + ","
                + fpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);

        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }
}
