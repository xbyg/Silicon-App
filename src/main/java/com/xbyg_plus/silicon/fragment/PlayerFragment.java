package com.xbyg_plus.silicon.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.activity.MainActivity;
import com.xbyg_plus.silicon.model.WebVideoInfo;
import com.xbyg_plus.silicon.utils.OKHTTPClient;
import com.xbyg_plus.silicon.widget.VideoPlayer;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerFragment extends Fragment{
    @BindView(R.id.video_player) VideoPlayer videoPlayer;
    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.views) TextView viewsView;
    @BindView(R.id.description) TextView descriptionView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_player, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        videoPlayer.setVideoPlayerListener(new VideoPlayer.VideoPlayerListener() {
            @Override
            public void onEnterFullscreenMode() {
                MainActivity activity = (MainActivity) getActivity();
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                activity.getSupportActionBar().hide();
                activity.getNavigation().setVisibility(View.GONE);
            }

            @Override
            public void onExitFullscreenMode() {
                MainActivity activity = (MainActivity) getActivity();
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                activity.getSupportActionBar().show();
                activity.getNavigation().setVisibility(View.VISIBLE);
            }
        });
    }

    public void prepare(WebVideoInfo videoInfo) {
        titleView.setText(videoInfo.title);
        viewsView.setText(getString(R.string.video_views, videoInfo.views));
        descriptionView.setText(videoInfo.description);
        videoPlayer.prepare(videoInfo);
    }


    private void likeVideo(WebVideoInfo videoInfo) {
        // rate() function in http://58.177.253.163/mtv/js/functions.js
        HashMap<String, String> postData = new HashMap<>();
        postData.put("mode", "rating");
        postData.put("id", String.valueOf(videoInfo.id));
        postData.put("rating", "5"); //is it a constant?
        postData.put("type", "video");

        OKHTTPClient.post("http://58.177.253.163/mtv/ajax.php", postData)
                .subscribe(htmlString -> {/*likeView.setColorFilter(Color.parseColor("#4F84C4"));*/},
                        throwable -> Snackbar.make(getView(), R.string.io_exception, Snackbar.LENGTH_SHORT).show());
    }
}
