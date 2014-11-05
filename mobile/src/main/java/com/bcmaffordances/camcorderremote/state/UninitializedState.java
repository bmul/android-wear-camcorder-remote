package com.bcmaffordances.camcorderremote.state;

import android.app.Activity;

/**
 * Created by bmullins on 11/5/14.
 */
public class UninitializedState extends AbstractRecordingState {

    public UninitializedState(Activity activity) {
        super(activity);
    }

    @Override
    public void updateDisplayedButtons() {
        disableAndHideButton(mRecordButton);
        disableAndHideButton(mResumeButton);
        disableAndHideButton(mPauseButton);
        disableAndHideButton(mStopButton);
    }
}
