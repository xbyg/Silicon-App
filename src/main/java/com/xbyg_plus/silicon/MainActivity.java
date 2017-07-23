package com.xbyg_plus.silicon;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.xbyg_plus.silicon.adapter.WebResourceRVAdapter;
import com.xbyg_plus.silicon.fragment.NoticeFragment;
import com.xbyg_plus.silicon.fragment.PastPaperFragment;
import com.xbyg_plus.silicon.fragment.PlayerFragment;
import com.xbyg_plus.silicon.fragment.UserFragment;
import com.xbyg_plus.silicon.fragment.VideoFragment;
import com.xbyg_plus.silicon.model.WebVideoInfo;
import com.xbyg_plus.silicon.utils.SchoolAccountHelper;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigation;

    private FragmentManager manager;

    private Fragment activeFragment = null;

    private VideoFragment videoFragment = new VideoFragment();
    private PlayerFragment playerFragment = new PlayerFragment();
    private NoticeFragment noticeFragment = new NoticeFragment();
    private PastPaperFragment pastPaperFragment = new PastPaperFragment();
    private UserFragment userFragment = new UserFragment();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = (item) -> {
        switch (item.getItemId()) {
            case R.id.navigation_videos:
                showFragment(videoFragment);
                return true;
            case R.id.navigation_notice:
                showFragment(noticeFragment);
                return true;
            case R.id.navigation_pastPaper:
                showFragment(pastPaperFragment);
                return true;
            case R.id.navigation_user:
                showFragment(userFragment);
                return true;
        }
        return false;
    };

    public void showFragment(Fragment target) {
        FragmentTransaction transaction = manager.beginTransaction();
        if (activeFragment != null) {
            transaction.hide(activeFragment);
            if (target.isAdded()) {
                transaction.show(target);
            } else {
                transaction.add(R.id.content, target);
            }
        } else {
            transaction.add(R.id.content, target);
        }
        transaction.commit();
        activeFragment = target;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = getSupportFragmentManager();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setItemIconTintList(null);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_videos);

        if (!SchoolAccountHelper.guestMode) {
            this.verifyPermission();
        }
    }

    private void verifyPermission() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Snackbar.make(this.findViewById(android.R.id.content), getString(R.string.write_permission_denied), BaseTransientBottomBar.LENGTH_LONG)
                    .setAction(R.string.retry, v -> this.verifyPermission())
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        if (userFragment.isVisible() || noticeFragment.isVisible() || videoFragment.isVisible() || (pastPaperFragment.isVisible() && pastPaperFragment.onBackPressed())) {
            moveTaskToBack(true);
        } else if (userFragment.getDownloadsFragment().isVisible() || userFragment.getSettingsFragment().isVisible() || userFragment.getAboutFragment().isVisible()) {
            showFragment(userFragment);
        } else if (playerFragment.isVisible()) {
            showFragment(videoFragment);
        }
    }

    /**
     * This function is used in WebResourceRVAdapter for showing the progress of download.
     *
     * @see WebResourceRVAdapter#showDownloadConfirm()
     */
    public void showDownloadsFragment() {
        /*
         * navigation.setSelectedItemId(R.id.navigation_user);
         *
         * The code above will re-create the UserFragment but before OKHTTPClient.call has responded,the getActivity() function return null since it has already replaced the DownloadsFragment.
         * Thus,app crashes
         * Another solution is call the OKHTTPClient.call inside onAttach() function,
         * but there is a danger that the view may not created when response arrived.'
         * */
        navigation.getMenu().getItem(3).setChecked(true);
        showFragment(userFragment.getDownloadsFragment());
    }

    public void showPlayerFragment(WebVideoInfo videoInfo) {
        if (playerFragment.getArguments() != null) {
            playerFragment.getArguments().putSerializable("videoInfo", videoInfo);
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable("videoInfo", videoInfo);
            playerFragment.setArguments(bundle);
        }
        showFragment(playerFragment);
    }
}
