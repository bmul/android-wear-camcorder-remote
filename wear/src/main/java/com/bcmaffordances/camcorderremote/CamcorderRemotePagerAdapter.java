package com.bcmaffordances.camcorderremote;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.wearable.view.FragmentGridPagerAdapter;

import com.bcmaffordances.camcorderremote.state.WearableRecordingStateContext;

/**
 * Pager adapter for controlling a camcorder from a wearable device.
 */
public class CamcorderRemotePagerAdapter extends FragmentGridPagerAdapter  {

    private static final String TAG = "CamcorderRemotePagerAdapter";
    private WearableRecordingStateContext mRecordingStateContext;

    /**
     * Constructor
     * @param fragmentManager FragmentManager
     * @param recordingStateContext The recording state context manager
     */
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
            // Delegate to the RecordingStateContext object to determine
            // which recording actions to make available based upon the
            // current recording state.
            return mRecordingStateContext.getRecordActionFragment();
        }
        if (1 == colIndex) {
            return mRecordingStateContext.getStopActionFragment();
        }
        return null;
    }
}
