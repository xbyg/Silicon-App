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
import com.xbyg_plus.silicon.adapter.PastPaperRVAdapter;
import com.xbyg_plus.silicon.utils.CachesDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class PastPaperFragment extends Fragment {
    @BindView(R.id.store_house_ptr_frame)
    PtrFrameLayout ptrFrame;
    @BindView(R.id.res_recycler_view)
    RecyclerView recyclerView;

    private PastPaperRVAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_past_paper, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, getView());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PastPaperRVAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        ptrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                adapter.refreshData();
                frame.refreshComplete();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CachesDatabase.save();
    }

    public boolean onBackPressed() {
        return ((PastPaperRVAdapter) recyclerView.getAdapter()).backFolder();
    }
}
