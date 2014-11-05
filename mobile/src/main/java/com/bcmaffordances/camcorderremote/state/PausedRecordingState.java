package com.bcmaffordances.camcorderremote.state;

import android.app.Activity;

/**
 * Created by bmullins on 11/5/14.
 */
public class PausedRecordingState extends AbstractRecordingState {

    public PausedRecordingState(Activity activity) {
        super(activity);
    }

    @Override
    public void updateDisplayedButtons() {
        disableAndHideButton(mPauseButton);
        disableAndHideButton(mStopButton);
        enableAndShowButton(mResumeButton);
    }
}
