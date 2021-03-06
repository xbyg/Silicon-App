package com.xbyg_plus.silicon.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xbyg_plus.silicon.activity.MainActivity;
import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.dialog.ChangePasswordDialog;
import com.xbyg_plus.silicon.dialog.LoginDialog;
import com.xbyg_plus.silicon.model.SchoolAccount;
import com.xbyg_plus.silicon.utils.SchoolAccountHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class UserFragment extends Fragment {
    private NotificationFragment notificationFragment = new NotificationFragment();
    private DownloadsFragment downloadsFragment = new DownloadsFragment();
    private SettingsFragment settingsFragment = new SettingsFragment();
    private AboutFragment aboutFragment = new AboutFragment();

    private SchoolAccountHelper accountHelper;
    private Unbinder unbinder;

    @BindView(R.id.name) TextView name;
    @BindView(R.id.edit) ImageView edit;
    @BindView(R.id.logout) ImageView logout;
    @BindView(R.id.login) TextView login;

    @BindView(R.id.notification_cardview) CardView notifications;
    @BindView(R.id.download_cardview) CardView downloads;
    @BindView(R.id.settings_cardview) CardView settings;
    @BindView(R.id.about_cardview) CardView about;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_nav_user, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        accountHelper = SchoolAccountHelper.getInstance();
        unbinder = ButterKnife.bind(this, view);

        if (!accountHelper.isGuestMode()) {
            ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog(getContext());

            SchoolAccount schoolAccount = accountHelper.getSchoolAccount();
            name.setText(getString(R.string.student_info, schoolAccount.getName(), schoolAccount.getClassRoom(), schoolAccount.getClassNo()));

            login.setVisibility(View.GONE);

            edit.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(getContext(), edit);
                popupMenu.inflate(R.menu.user_edit);
                popupMenu.getMenu().findItem(R.id.change_pwd);

                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.change_pwd) {
                        changePasswordDialog.setContent().show();
                    }
                    return true;
                });

                MenuPopupHelper menuHelper = new MenuPopupHelper(getContext(), (MenuBuilder) popupMenu.getMenu(), edit);
                menuHelper.setForceShowIcon(true);
                menuHelper.show();
            });

            logout.setOnClickListener(v -> {
                accountHelper.logout();
                restartActivity();
            });
        } else {
            LoginDialog loginDialog = new LoginDialog(getContext());

            name.setText(R.string.guest);
            edit.setVisibility(View.GONE);
            logout.setVisibility(View.GONE);

            login.setOnClickListener(v -> loginDialog.show());

            loginDialog.setLoginAction(this::restartActivity);
        }

        notifications.setOnClickListener(v -> ((MainActivity) getActivity()).showFragment(notificationFragment));

        downloads.setOnClickListener(v -> ((MainActivity) getActivity()).showFragment(downloadsFragment));

        settings.setOnClickListener(v -> ((MainActivity) getActivity()).showFragment(settingsFragment));

        about.setOnClickListener(v -> ((MainActivity) getActivity()).showFragment(aboutFragment));
    }

    private void restartActivity() {
        getActivity().finish();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public NotificationFragment getNotificationFragment() {
        return notificationFragment;
    }

    public DownloadsFragment getDownloadsFragment() {
        return this.downloadsFragment;
    }

    public SettingsFragment getSettingsFragment() {
        return this.settingsFragment;
    }

    public AboutFragment getAboutFragment() {
        return aboutFragment;
    }
}
