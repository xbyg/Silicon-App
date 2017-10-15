package com.xbyg_plus.silicon.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.data.repository.NotificationRepository;
import com.xbyg_plus.silicon.fragment.adapter.NotificationRVAdapter;
import com.xbyg_plus.silicon.service.NotificationService.NotificationReceivedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationFragment extends Fragment {
    @BindView(R.id.notification_recycler_view) RecyclerView recyclerView;
    private NotificationRVAdapter adapter;
    private LinearLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_notification, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        adapter = new NotificationRVAdapter(NotificationRepository.instance.getData(false));
        layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotificationReceived(NotificationReceivedEvent event) {
        layoutManager.scrollToPosition(adapter.getItemCount() - 1);
        adapter.notifyItemInserted(adapter.getItemCount() - 1);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
