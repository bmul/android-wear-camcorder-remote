package com.bcmaffordances.camcorderremote;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.wearable.view.FragmentGridPagerAdapter;

import com.bcmaffordances.camcorderremote.state.WearableRecordingStateContext;

/**
 * Pager adapter for controlling a camcorder.
 */
public class CamcorderRemotePagerAdapter extends FragmentGridPagerAdapter  {

    private static final String TAG = "CamcorderRemotePagerAdapter";
    private WearableRecordingStateContext mRecordingStateContext;

    public CamcorderRemotePagerAdapter(FragmentManager fragmentManager,
                                       WearableRecordingStateContext recordingStateContext) {
        super(fragmentManager);
        mRecordingStateContext = recordingStateContext;
    }

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount(int i) {
        return 2;
    }

    @Override
    public Fragment getFragment(int rowIndex, int colIndex) {
        if (0 == colIndex) {
            return mRecordingStateContext.getRecordActionFragment();
        }
        if (1 == colIndex) {
            return mRecordingStateContext.getStopActionFragment();
        }
        return null;
    }
}
