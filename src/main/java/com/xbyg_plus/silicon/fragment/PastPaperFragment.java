package com.xbyg_plus.silicon.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.data.repository.PastPaperRepository;
import com.xbyg_plus.silicon.fragment.adapter.PastPaperRVAdapter;
import com.xbyg_plus.silicon.utils.SchoolAccountHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class PastPaperFragment extends Fragment {
    @BindView(R.id.store_house_ptr_frame) PtrFrameLayout ptrFrame;
    @BindView(R.id.res_recycler_view) RecyclerView recyclerView;

    private PastPaperRVAdapter adapter;
    private LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_nav_past_paper, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, getView());

        if (!SchoolAccountHelper.getInstance().isGuestMode()) {
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            adapter = new PastPaperRVAdapter(getActivity(), PastPaperRepository.instance.getData(true));
            recyclerView.setAdapter(adapter);

            ptrFrame.setPtrHandler(new PtrHandler() {
                @Override
                public void onRefreshBegin(PtrFrameLayout frame) {
                    adapter.refreshData();
                    frame.refreshComplete();
                }

                @Override
                public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                    return layoutManager.findFirstCompletelyVisibleItemPosition() == 0;
                }
            });
        } else {
            recyclerView.setVisibility(View.GONE);

            TextView guestText = new TextView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.leftMargin = 30;
            params.rightMargin = 30;
            guestText.setText(getString(R.string.guest_text, getString(R.string.past_papers).toLowerCase()));
            guestText.setTextSize(20);
            guestText.setGravity(Gravity.CENTER);
            ((ViewGroup) ptrFrame.getContentView()).addView(guestText, params);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        PastPaperRepository.instance.applyData().subscribe();
    }

    public boolean onBackPressed() {
        return ((PastPaperRVAdapter) recyclerView.getAdapter()).backFolder();
    }
}
