package com.bcmaffordances.camcorderremote.state;

import android.app.Activity;

/**
 * Created by bmullins on 11/5/14.
 */
public class ResumedRecordingState extends AbstractRecordingState {

    public ResumedRecordingState(Activity activity) {
        super(activity);
    }

    @Override
    public void updateDisplayedButtons() {
        disableAndHideButton(mResumeButton);
        enableAndShowButton(mPauseButton);
        enableAndShowButton(mStopButton);
    }
}
