package com.bcmaffordances.camcorderremote.state;

import com.bcmaffordances.camcorderremote.ActionFragment;
import com.bcmaffordances.wearableconnector.WearableConnector;

/**
 * Created by bmullins on 11/2/14.
 */
public interface WearableRecordingState {

    /**
     * Encapsulate the business logic of determining which record action
     * is available based upon the current recording state.
     * Can be one of: Record (for new recordings), Pause, or Resume.
     *
     * @param wearableConnector Optional parameter to allow the generated ActionFragment
     *                          to send messages.
     */
    public ActionFragment getRecordActionFragment(WearableConnector wearableConnector);

    /**
     * Return an ActionFragment to stop recording.
     * @param wearableConnector
     * @return
     */
    public ActionFragment getStopActionFragment(WearableConnector wearableConnector);
}
