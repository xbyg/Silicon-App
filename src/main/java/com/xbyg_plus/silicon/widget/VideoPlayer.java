package com.xbyg_plus.silicon.widget;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.model.WebVideoInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class VideoPlayer extends RelativeLayout implements SurfaceHolder.Callback {
    @BindView(R.id.video_surface) SurfaceView videoSurface;
    @BindView(R.id.controller_layout) RelativeLayout controllerLayout;
    @BindView(R.id.play_btn) ImageView playBtn;
    @BindView(R.id.progress_text) TextView progressText;
    @BindView(R.id.progress_bar) SeekBar progressBar;
    @BindView(R.id.duration) TextView durationView;
    @BindView(R.id.fullscreen) ImageView fullscreenView;

    private static final int UPDATE_PROGRESS = 1;

    private MediaPlayer mediaPlayer = new MediaPlayer();

    private boolean isFullscreenMode = false;
    //always check whether has started updating the progress before sending UPDATE_PROGRESS, it avoids updating the progress with multiple times in one period
    private boolean isUpdatingProgress = false;

    private Handler progressHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_PROGRESS:
                    int ms = mediaPlayer.getCurrentPosition();
                    progressBar.setProgress(ms);
                    int second = ms / 1000, hh = second / 3600, mm = second % 3600 / 60, ss = second % 60;
                    progressText.setText(hh == 0 ? String.format("%02d:%02d", mm, ss) : String.format("%02d:%02d:%02d", hh, mm, ss));
                    this.sendEmptyMessageDelayed(UPDATE_PROGRESS, 500);
                    break;
            }
        }
    };

    private VideoPlayerListener listener;

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        //setting focusable to true, we can handle the key event
        this.setFocusableInTouchMode(true);

        inflate(getContext(), R.layout.widget_video_player, this);
        ButterKnife.bind(this);
        this.setVisibility(GONE);
        videoSurface.getHolder().addCallback(this);

        playBtn.setOnClickListener(v -> {
            if (!mediaPlayer.isPlaying()) {
                play();
            } else {
                pause();
            }
        });

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int second = progress / 1000, hh = second / 3600, mm = second % 3600 / 60, ss = second % 60;
                progressText.setText(hh == 0 ? String.format("%02d:%02d", mm, ss) : String.format("%02d:%02d:%02d", hh, mm, ss));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //if video is playing, then keep playing it, don't need to pause
                progressHandler.removeMessages(UPDATE_PROGRESS);
                isUpdatingProgress = false;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(progressBar.getProgress());
                if (!mediaPlayer.isPlaying()) {
                    //if video is paused by user before tracking touch, then play it when tracking is done
                    mediaPlayer.start();
                }
                progressHandler.sendEmptyMessage(UPDATE_PROGRESS);
                isUpdatingProgress = true;
            }
        });

        fullscreenView.setOnClickListener(v -> setFullscreenMode(!isFullscreenMode));
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mediaPlayer.setDisplay(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        this.pause();
        mediaPlayer.setDisplay(null);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && isFullscreenMode) {
            //consume this event and exit full screen mode
            setFullscreenMode(false);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                if (controllerLayout.getVisibility() == GONE) {
                    if (mediaPlayer.isPlaying() && !isUpdatingProgress) {
                        progressHandler.sendEmptyMessage(UPDATE_PROGRESS);
                    }
                    controllerLayout.setVisibility(VISIBLE);
                } else {
                    controllerLayout.setVisibility(GONE);
                    progressHandler.removeMessages(UPDATE_PROGRESS);
                    isUpdatingProgress = false;
                }
                break;
        }
        return true;
    }

    public interface VideoPlayerListener {
        void onEnterFullscreenMode();

        void onExitFullscreenMode();
    }

    public void setVideoPlayerListener(VideoPlayerListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isFullscreenMode = true;
            listener.onEnterFullscreenMode();
            ViewGroup.LayoutParams lp = this.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            setLayoutParams(lp);
            resizeSurfaceSize();
        } else {
            isFullscreenMode = false;
            listener.onExitFullscreenMode();
            ViewGroup.LayoutParams lp = this.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            setLayoutParams(lp);
            resizeSurfaceSize();
        }
    }

    public Completable prepare(WebVideoInfo videoInfo) {
        this.setVisibility(VISIBLE);
        return Completable.mergeArray(prepareMediaPlayer(videoInfo), prepareViews(videoInfo))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    this.resizeSurfaceSize();
                    mediaPlayer.start();
                });
    }

    private Completable prepareMediaPlayer(WebVideoInfo videoInfo) {
        return Completable.create((CompletableEmitter e) -> {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(videoInfo.videoAddress);
            mediaPlayer.setWakeMode(getContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.prepareAsync(); //some devices have to prepare asynchronously?
            mediaPlayer.setOnPreparedListener(mp -> e.onComplete());
        }).subscribeOn(Schedulers.io());
    }

    private Completable prepareViews(WebVideoInfo videoInfo) {
        return Completable.create((CompletableEmitter e) -> {
            durationView.setText(videoInfo.formattedDuration);
            playBtn.setImageResource(R.drawable.pause);
            progressBar.setMax(videoInfo.duration);
            progressBar.setProgress(0);
            e.onComplete();
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    private void resizeSurfaceSize() {
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
        int screenWidth = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();

        ViewGroup.LayoutParams surfaceLayoutParams = videoSurface.getLayoutParams();
        surfaceLayoutParams.width = screenWidth;
        surfaceLayoutParams.height = screenWidth * videoHeight / videoWidth;
        videoSurface.setLayoutParams(surfaceLayoutParams);
    }

    public void play() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            playBtn.setImageResource(R.drawable.pause);
            if (controllerLayout.getVisibility() == VISIBLE && !isUpdatingProgress) {
                progressHandler.sendEmptyMessage(UPDATE_PROGRESS);
                isUpdatingProgress = true;
            } else {
                progressHandler.removeMessages(UPDATE_PROGRESS);
                isUpdatingProgress = false;
            }
        }
    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playBtn.setImageResource(R.drawable.play);
            progressHandler.removeMessages(UPDATE_PROGRESS);
            isUpdatingProgress = false;
        }
    }

    public void release() {
        progressHandler.removeMessages(UPDATE_PROGRESS);
        isUpdatingProgress = false;
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    public void setFullscreenMode(boolean enable) {
        if (enable) {
            ((Activity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            fullscreenView.setImageResource(R.drawable.full_screen_off);
        } else {
            ((Activity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            fullscreenView.setImageResource(R.drawable.full_screen_on);
        }
    }
}
