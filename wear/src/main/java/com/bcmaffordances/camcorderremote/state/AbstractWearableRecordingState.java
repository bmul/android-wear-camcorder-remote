package com.bcmaffordances.camcorderremote.state;

import android.view.View;

import com.bcmaffordances.camcorderremote.ActionFragment;
import com.bcmaffordances.camcorderremote.R;
import com.bcmaffordances.camcorderremotecommon.CamcorderRemoteConstants;
import com.bcmaffordances.wearableconnector.WearableConnector;

/**
 * Created by bmullins on 11/3/14.
 */
public abstract class AbstractWearableRecordingState implements WearableRecordingState {

    @Override
    public ActionFragment getStopActionFragment(final WearableConnector wearableConnector) {
        final ActionFragment actionFragment = ActionFragment.create(R.drawable.stop, R.string.stop, null);
        actionFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wearableConnector.sendMessage(
                        CamcorderRemoteConstants.MESSAGE_PATH,
                        CamcorderRemoteConstants.REQUEST_STOP);
            }
        });
        return actionFragment;
    }
}
