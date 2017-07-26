package com.xbyg_plus.silicon.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.adapter.MTVPagerAdapter;
import com.xbyg_plus.silicon.model.WebVideoInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MTVFragment extends Fragment{
    @BindView(R.id.view_pager) ViewPager viewPager;
    private VideoFragment videoFragment = new VideoFragment();
    private PlayerFragment playerFragment = new PlayerFragment();

    private MTVPagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_nav_mtv, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        pagerAdapter = new MTVPagerAdapter(getChildFragmentManager(), this);
        viewPager.setAdapter(pagerAdapter);
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public VideoFragment getVideoFragment() {
        return videoFragment;
    }

    public PlayerFragment getPlayerFragment() {
        return playerFragment;
    }

    public void playVideo(WebVideoInfo videoInfo) {
        viewPager.setCurrentItem(1, true);
        playerFragment.getVideoPlayer().prepare(videoInfo);
    }
}
