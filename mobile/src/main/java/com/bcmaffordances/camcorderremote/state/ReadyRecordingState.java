package com.bcmaffordances.camcorderremote.state;

import android.app.Activity;

/**
 * Created by bmullins on 11/5/14.
 */
public class ReadyRecordingState extends AbstractRecordingState {

    public ReadyRecordingState(Activity activity) {
        super(activity);
    }

    @Override
    public void updateDisplayedButtons() {
        enableAndShowButton(mRecordButton);
        disableAndHideButton(mResumeButton);
        disableAndHideButton(mPauseButton);
        disableAndHideButton(mStopButton);
    }
}
