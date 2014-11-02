package com.bcmaffordances.camcorderremote;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.wearable.view.FragmentGridPagerAdapter;

/**
 * Pager adapter for controlling a camcorder.
 */
public class CamcorderRemotePagerAdapter extends FragmentGridPagerAdapter  {

    public CamcorderRemotePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount(int i) {
        return 1;
    }

    @Override
    public Fragment getFragment(int rowIndex, int colIndex) {
        return new ActionFragment();
    }
}
