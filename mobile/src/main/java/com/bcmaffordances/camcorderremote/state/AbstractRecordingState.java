package com.bcmaffordances.camcorderremote.state;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;

import com.bcmaffordances.camcorderremote.R;

/**
 * Created by bmullins on 10/30/14.
 */
abstract public class AbstractRecordingState implements RecordingState {

    protected ImageButton mRecordButton, mResumeButton, mPauseButton, mStopButton;

    // TODO set activity as member var so that all states don't need to pass in c'tor

    public AbstractRecordingState(Activity activity) {
        mRecordButton = (ImageButton) activity.findViewById(R.id.recordButton);
        mResumeButton = (ImageButton) activity.findViewById(R.id.resumeButton);
        mPauseButton = (ImageButton) activity.findViewById(R.id.pauseButton);
        mStopButton = (ImageButton) activity.findViewById(R.id.stopButton);
    }

    @Override
    public void updateDisplayedButtons() {
        // Left for concrete classes to fill in.
    }

    protected void enableAndShowButton(ImageButton button) {
        button.setEnabled(true);
        button.setVisibility(View.VISIBLE);
    }

    protected void disableAndHideButton(ImageButton button) {
        button.setEnabled(false);
        button.setVisibility(View.GONE);
    }
}
