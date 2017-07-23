package com.xbyg_plus.silicon.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.view.IconTextView;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutFragment extends Fragment {
    private final static LinkedHashMap<Integer, String> iconMap = new LinkedHashMap<>();
    private final static LinkedHashMap<String, String> libraryMap = new LinkedHashMap<>();

    static {
        iconMap.put(R.drawable.icon, "Freepik");
        iconMap.put(R.drawable.videos, "Vectors Market");
        iconMap.put(R.drawable.notice, "Vectors Market");
        iconMap.put(R.drawable.past_paper, "Vectors Market");
        iconMap.put(R.drawable.user, "Madebyoliver");
        iconMap.put(R.drawable.filter2, "Freepik");
        iconMap.put(R.drawable.mail, "Madebyoliver");
        iconMap.put(R.drawable.view, "Cole Bemis");
        iconMap.put(R.drawable.folder, "Madebyoliver");
        iconMap.put(R.drawable.file, "Madebyoliver");
        iconMap.put(R.drawable.edit, "Gregor Cresnar");
        iconMap.put(R.drawable.logout, "Gregor Cresnar");
        iconMap.put(R.drawable.key, "Freepik");
        iconMap.put(R.drawable.download2, "Madebyoliver");
        iconMap.put(R.drawable.delete2, "Freepik");
        iconMap.put(R.drawable.cancel, "Madebyoliver");
        iconMap.put(R.drawable.crying, "Freepik");
        iconMap.put(R.drawable.arrow, "Gregor Cresnar");
        iconMap.put(R.drawable.back, "Gregor Cresnar");

        libraryMap.put("okhttp(v3.8.0)", "square");
        libraryMap.put("picasso(v2.5.2)", "square");
        libraryMap.put("jsoup(v1.10.2)", "jhy");
        libraryMap.put("js-evaluator-for-android(v2.0.0)", "evgenyneu");
        libraryMap.put("sdp(v1.0.4)", "intuit");
        libraryMap.put("logger(v2.1.1)", "orhanobut");
        libraryMap.put("EventBus(v3.0.0)", "greenrobot");
        libraryMap.put("gson(v2.8.1)", "google");
        libraryMap.put("android-Ultra-Pull-To-Refresh(v1.0.11)", "liaohuqiu");
        libraryMap.put("butterknife(v8.6.0)", "JakeWharton");
    }

    @BindView(R.id.iconList) LinearLayout iconsList;
    @BindView(R.id.libraryList) LinearLayout libraryList;
    private final LinearLayout.LayoutParams itemLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_about, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        itemLayoutParams.topMargin = 15;

        for (Map.Entry<Integer, String> entry : iconMap.entrySet()) {
            this.addIconSource(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, String> entery : libraryMap.entrySet()) {
            this.addLibrarySource(entery.getKey(), entery.getValue());
        }
    }

    public void addIconSource(int id, String author) {
        IconTextView iconTextView = new IconTextView(getContext(), id, 128, "Designed by " + author);
        iconTextView.setLayoutParams(itemLayoutParams);
        iconsList.addView(iconTextView);
    }

    public void addLibrarySource(String name, String author) {
        IconTextView iconTextView = new IconTextView(getContext(), R.drawable.coffee_cup, 128, name + " by " + author);
        iconTextView.setLayoutParams(itemLayoutParams);
        libraryList.addView(iconTextView);
    }
}
