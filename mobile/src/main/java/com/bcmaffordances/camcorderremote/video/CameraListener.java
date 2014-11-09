package com.bcmaffordances.camcorderremote.video;

/**
 * Created by bmullins on 11/9/14.
 */
public interface CameraListener {

    /**
     * Callback to be invoked after Camera is initialized.
     */
    public void onInit();

    /**
     * Callback to be invoked after Camera is released.
     */
    public void onRelease();
}
