package com.bcmaffordances.camcorderremote.video;

/**
 * Created by bmullins on 11/9/14.
 */
public interface CameraListener {

    /**
     * Callback to be invoked on Camera initialization.
     */
    public void onInit();

    /**
     * Callback to be invoked once Camera is released.
     */
    public void onRelease();
}
