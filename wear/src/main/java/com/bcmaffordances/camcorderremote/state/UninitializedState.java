package com.bcmaffordances.camcorderremote.state;

import com.bcmaffordances.camcorderremote.ActionFragment;
import com.bcmaffordances.camcorderremote.R;
import com.bcmaffordances.wearableconnector.WearableConnector;

/**
 * Created by bmullins on 11/2/14.
 */
public class UninitializedState extends AbstractWearableRecordingState {

    @Override
    public ActionFragment getRecordActionFragment(WearableConnector wearableConnector) {
        final ActionFragment actionFragment = ActionFragment.create(R.drawable.ic_full_sad, R.string.connecting, null);
        actionFragment.setOnClickListener(null);
        return actionFragment;
    }
}
