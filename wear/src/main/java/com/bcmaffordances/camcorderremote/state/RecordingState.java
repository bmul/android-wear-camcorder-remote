package com.bcmaffordances.camcorderremote.state;

import android.view.View;

import com.bcmaffordances.camcorderremote.ActionFragment;
import com.bcmaffordances.camcorderremote.R;
import com.bcmaffordances.wearableconnector.CamcorderRemoteConstants;
import com.bcmaffordances.wearableconnector.WearableConnector;

/**
 * Created by bmullins on 11/3/14.
 */
public class RecordingState extends AbstractWearableRecordingState {
    @Override
    public ActionFragment getRecordActionFragment(final WearableConnector wearableConnector) {
        final ActionFragment actionFragment = ActionFragment.create(R.drawable.ic_full_sad, R.string.pause, null);
        actionFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wearableConnector.sendMessage(CamcorderRemoteConstants.REQUEST_PAUSE);
            }
        });
        return actionFragment;
    }
}
