package com.xbyg_plus.silicon.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.dialog.LoadingDialog;
import com.xbyg_plus.silicon.fragment.adapter.MTVPagerAdapter;
import com.xbyg_plus.silicon.model.WebVideoInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MTVFragment extends Fragment{
    @BindView(R.id.view_pager) ViewPager viewPager;
    private VideoFragment videoFragment = new VideoFragment();
    private PlayerFragment playerFragment = new PlayerFragment();

    private LoadingDialog loadingDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_nav_mtv, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        loadingDialog = new LoadingDialog(getContext());

        viewPager.setAdapter(new MTVPagerAdapter(getChildFragmentManager(), this));
    }

    public VideoFragment getVideoFragment() {
        return videoFragment;
    }

    public PlayerFragment getPlayerFragment() {
        return playerFragment;
    }

    public void playVideo(WebVideoInfo videoInfo) {
        loadingDialog.setMessage(R.string.preparing_video).show();

        playerFragment.prepare(videoInfo).subscribe(() -> {
            viewPager.setCurrentItem(1, true);
            loadingDialog.dismiss();
        });
    }
}
