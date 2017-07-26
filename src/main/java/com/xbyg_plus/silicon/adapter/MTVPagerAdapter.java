package com.xbyg_plus.silicon.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.xbyg_plus.silicon.fragment.MTVFragment;

public class MTVPagerAdapter extends FragmentPagerAdapter{
    private MTVFragment mtvFragment;

    public MTVPagerAdapter(FragmentManager fragmentManager, MTVFragment mtvFragment) {
        super(fragmentManager);
        this.mtvFragment = mtvFragment;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return mtvFragment.getVideoFragment();
            case 1:
                return mtvFragment.getPlayerFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
