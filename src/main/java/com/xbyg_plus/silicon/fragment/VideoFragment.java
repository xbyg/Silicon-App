package com.xbyg_plus.silicon.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.dialog.DialogManager;
import com.xbyg_plus.silicon.fragment.adapter.VideoRVAdapter;
import com.xbyg_plus.silicon.dialog.FilterDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class VideoFragment extends Fragment implements DialogManager.DialogHolder {
    @BindView(R.id.store_house_ptr_frame) PtrClassicFrameLayout ptrFrame;
    @BindView(R.id.videos_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.filter_btn) FloatingActionButton filterBtn;

    private VideoRVAdapter adapter;
    private LinearLayoutManager layoutManager;

    private FilterDialog filterDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_videos, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        DialogManager.registerDialogHolder(this);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new VideoRVAdapter(getActivity(), (MTVFragment) getParentFragment());
        recyclerView.setAdapter(adapter);

        filterBtn.setOnClickListener(v -> {
            filterDialog
                    .setRequestFilter(adapter.getRequestFilter())
                    .show();
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1 && layoutManager.getItemCount() != 0) {
                    //layoutManager.getItemCount() != 0  avoid load more videos when refreshing the data
                    adapter.loadMoreVideos();
                }
            }
        });
        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(itemDecoration);

        ptrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                layoutManager.scrollToPosition(0);
                adapter.refreshData();
                frame.refreshComplete();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return layoutManager.findFirstCompletelyVisibleItemPosition() == 0;
            }
        });
    }

    @Override
    public void onDialogsCreated(DialogManager dialogManager) {
        this.filterDialog = dialogManager.obtain(FilterDialog.class)
                .setFilterOptionsSelectedListener(() -> ptrFrame.autoRefresh(true));
    }
}
