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
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.xbyg_plus.silicon.adapter.WebResourceRVAdapter;
import com.xbyg_plus.silicon.fragment.NoticeFragment;
import com.xbyg_plus.silicon.fragment.PastPaperFragment;
import com.xbyg_plus.silicon.fragment.UserFragment;

public class MainActivity extends AppCompatActivity {
    // TODO: http://www.mosttss.edu.hk/websys/actsys/

    private BottomNavigationView navigation;

    private FragmentManager manager;
    private NoticeFragment noticeFragment;
    private PastPaperFragment pastPaperFragment;
    private UserFragment userFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_notice:
                    manager.beginTransaction().replace(R.id.content, noticeFragment).commit();
                    return true;
                case R.id.navigation_pastPaper:
                    manager.beginTransaction().replace(R.id.content, pastPaperFragment).commit();
                    return true;
                case R.id.navigation_user:
                    manager.beginTransaction().replace(R.id.content,userFragment).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = getSupportFragmentManager();
        noticeFragment = new NoticeFragment();
        pastPaperFragment = new PastPaperFragment();
        userFragment = new UserFragment();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setItemIconTintList(null);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_notice);

        this.verifyPermission();
    }

    private void verifyPermission(){
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_DENIED){
            Snackbar.make(this.findViewById(android.R.id.content),getString(R.string.write_permission_denied), BaseTransientBottomBar.LENGTH_LONG)
                    .setAction(R.string.retry, v->this.verifyPermission())
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        if(pastPaperFragment.isVisible() && pastPaperFragment.onBackPressed()){
            moveTaskToBack(true);
        }else if(userFragment.isVisible() || noticeFragment.isVisible()){
            moveTaskToBack(true);
        }else if(userFragment.getDownloadsFragment().isVisible() || userFragment.getSettingsFragment().isVisible() || userFragment.getAboutFragment().isVisible()){
            manager.beginTransaction().replace(R.id.content,userFragment).commit();
        }
    }

    /**
     * This function is used in WebResourceRVAdapter for showing the progress of download.
     * @see WebResourceRVAdapter#showDownloadConfirm()
     * */
    public void showDownloadsFragment(){
        /*
         * navigation.setSelectedItemId(R.id.navigation_user);
         *
         * The code above will re-create the UserFragment but before OKHTTPClient.call has responded,the getActivity() function return null since it has already replaced the DownloadsFragment.
         * Thus,app crashes
         * Another solution is call the OKHTTPClient.call inside onAttach() function,
         * but there is a danger that the view may not created when response arrived.'
         * */
        navigation.getMenu().getItem(2).setChecked(true);
        manager.beginTransaction().replace(R.id.content,userFragment.getDownloadsFragment()).commit();
    }
}
