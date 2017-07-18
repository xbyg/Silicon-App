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

import com.xbyg_plus.silicon.LoginActivity;
import com.xbyg_plus.silicon.callback.LogoutCallback;
import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.dialog.ChangePasswordDialog;
import com.xbyg_plus.silicon.model.SchoolAccount;
import com.xbyg_plus.silicon.utils.SchoolAccountHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class UserFragment extends Fragment{
    private DownloadsFragment downloadsFragment = new DownloadsFragment();
    private SettingsFragment settingsFragment = new SettingsFragment();
    private AboutFragment aboutFragment = new AboutFragment();

    private Unbinder unbinder;

    @BindView(R.id.name) TextView name;
    @BindView(R.id.download_cardview) CardView downloads;
    @BindView(R.id.settings_cardview) CardView settings;
    @BindView(R.id.about_cardview) CardView about;
    @BindView(R.id.edit) ImageView edit;
    @BindView(R.id.logout) ImageView logout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_user,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this,view);

        SchoolAccount schoolAccount = SchoolAccountHelper.getInstance().getSchoolAccount();
        name.setText(schoolAccount.getName()+"("+schoolAccount.getClassRoom()+schoolAccount.getClassNo()+")");

        downloads.setOnClickListener(v->{getFragmentManager().beginTransaction().replace(R.id.content,downloadsFragment).commit();});
        settings.setOnClickListener(v->{getFragmentManager().beginTransaction().replace(R.id.content,settingsFragment).commit();});
        about.setOnClickListener(v->{getFragmentManager().beginTransaction().replace(R.id.content,aboutFragment).commit();});

        edit.setOnClickListener(v->{
            PopupMenu popupMenu = new PopupMenu(getContext(), edit);
            popupMenu.inflate(R.menu.user_edit);
            popupMenu.setOnMenuItemClickListener( item -> {
                if(item.getItemId() == R.id.change_pwd) {
                    new ChangePasswordDialog(getContext());
                }
                return true;
            });

            MenuPopupHelper menuHelper = new MenuPopupHelper(getContext(), (MenuBuilder) popupMenu.getMenu(), edit);
            menuHelper.setForceShowIcon(true);
            menuHelper.show();
        });

        logout.setOnClickListener(v->{
            SchoolAccountHelper.getInstance().logout(new LogoutCallback(){
                @Override
                public void onLoggedOut() {
                    getActivity().finish();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }}
            );
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public DownloadsFragment getDownloadsFragment(){
        return this.downloadsFragment;
    }

    public SettingsFragment getSettingsFragment(){
        return this.settingsFragment;
    }

    public AboutFragment getAboutFragment() {
        return aboutFragment;
    }
}
