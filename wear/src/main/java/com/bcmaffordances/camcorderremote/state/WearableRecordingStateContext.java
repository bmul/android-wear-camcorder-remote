package com.bcmaffordances.camcorderremote.state;

import com.bcmaffordances.camcorderremote.ActionFragment;
import com.bcmaffordances.camcorderremotecommon.CamcorderRemoteConstants;
import com.bcmaffordances.wearableconnector.WearableConnector;

/**
 * Class to encapsulate the recording state on the wearable.
 */
public class WearableRecordingStateContext {

    private WearableRecordingState mCurrentState;
    private WearableConnector mWearableConnector;

    /**
     * Constructor
     * @param wearableConnector WearableConnector object to allow communication with mobile app.
     */
    public WearableRecordingStateContext(WearableConnector wearableConnector) {
        mWearableConnector = wearableConnector;
        mCurrentState = new UninitializedState();
    }

    /**
     * Change the recording state.
     * @param newState The new WearableRecordingState
     */
    public void changeState(WearableRecordingState newState) {
        mCurrentState = newState;
    }

    /**
     * Change the recording state.
     * This is a convenience class to allow changing the recording state
     * based upon the message received from the mobile app.
     * @param message Mobile app's response to a request from the wearable app.
     */
    public void changeState(String message) {
        if (message.equals(CamcorderRemoteConstants.RESPONSE_RECORDING) ||
                message.equals(CamcorderRemoteConstants.RESPONSE_RESUMED))
        {
            mCurrentState = new RecordingState();
        }
        else if (message.equals(CamcorderRemoteConstants.RESPONSE_PAUSED)) {
            mCurrentState = new PausedState();
        }
        else if (message.equals(CamcorderRemoteConstants.RESPONSE_STOPPED)) {
            mCurrentState = new ReadyState();
        }
    }

    /**
     * Get an ActionFragment for the current recording state.
     * @return ActionFragment object
     */
    public ActionFragment getRecordActionFragment() {
        return mCurrentState.getRecordActionFragment(mWearableConnector);
    }

    /**
     * Get an ActionFragment for the current recording state.
     * @return ActionFragment object
     */
    public ActionFragment getStopActionFragment() {
        return mCurrentState.getStopActionFragment(mWearableConnector);
    }
}
