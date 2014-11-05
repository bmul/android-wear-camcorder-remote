package com.bcmaffordances.camcorderremote.state;

import android.app.Activity;

/**
 * Created by bmullins on 11/5/14.
 */
public class RecordingStateContext {

    private RecordingState currentState;

    public RecordingStateContext(Activity recordingActivity) {
        currentState = new UninitializedState(recordingActivity);
    }

    public void changeState(RecordingState newState) {
        currentState = newState;
    }

    public void updateDisplayedButtons() {
        currentState.updateDisplayedButtons();
    }
}
