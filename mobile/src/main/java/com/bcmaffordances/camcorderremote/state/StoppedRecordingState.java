package com.bcmaffordances.camcorderremote.state;

import android.app.Activity;

/**
 * Created by bmullins on 11/5/14.
 */
public class StoppedRecordingState extends AbstractRecordingState {

    public StoppedRecordingState(Activity activity) {
        super(activity);
    }

    @Override
    public void updateDisplayedButtons() {
        disableAndHideButton(mStopButton);
        disableAndHideButton(mPauseButton);
        enableAndShowButton(mRecordButton);
    }
}
