package com.bcmaffordances.camcorderremote.state;

import android.app.Activity;

/**
 * Created by bmullins on 11/5/14.
 */
public class StartedRecordingState extends AbstractRecordingState {

    public StartedRecordingState(Activity activity) {
        super(activity);
    }

    @Override
    public void updateDisplayedButtons() {
        disableAndHideButton(mRecordButton);
        enableAndShowButton(mPauseButton);
        enableAndShowButton(mStopButton);
    }
}
