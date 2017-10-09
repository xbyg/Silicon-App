package com.xbyg_plus.silicon.fragment.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.fragment.MTVFragment;
import com.xbyg_plus.silicon.fragment.adapter.infoloader.WebVideoInfoLoader;
import com.xbyg_plus.silicon.model.WebVideoInfo;
import com.xbyg_plus.silicon.utils.TwoWayMap;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class VideoRVAdapter extends WebResourceRVAdapter<VideoRVAdapter.ViewHolder, WebVideoInfo, WebVideoInfoLoader> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView views;
        TextView duration;

        ViewHolder(View root) {
            super(root);
            image = root.findViewById(R.id.image);
            title = root.findViewById(R.id.title);
            views = root.findViewById(R.id.views);
            duration = root.findViewById(R.id.duration);
        }
    }

    private MTVFragment mtvFragment;
    private RequestFilter requestFilter;

    //it shows when loading image
    private ColorDrawable imgPlaceHolder = new ColorDrawable(Color.parseColor("#898E8C"));

    public VideoRVAdapter(Activity activity, MTVFragment mtvFragment) {
        super(activity);
        this.mtvFragment = mtvFragment;
        this.infoLoader = new WebVideoInfoLoader(activity);
        this.requestFilter = new RequestFilter(activity.getResources());
        loadMoreVideos();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WebVideoInfo videoInfo = this.resourcesList.get(position);

        holder.title.setText(videoInfo.title);
        holder.views.setText(activity.getString(R.string.video_views, videoInfo.views));
        holder.duration.setText(videoInfo.formattedDuration);
        Picasso.with(activity).load(videoInfo.imgAddress).placeholder(imgPlaceHolder).into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            infoLoader.resolveVideoDetails(videoInfo)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> mtvFragment.playVideo(videoInfo));
        });
    }

    @Override
    public void refreshData() {
        resourcesList.clear();
        loadMoreVideos();
    }

    public int getPagesLoaded() {
        return this.resourcesList.size() / 30;
    }

    public boolean canLoadMore() {
        return this.resourcesList.size() % 30 == 0;
    }

    public void loadMoreVideos() {
        if (canLoadMore()) {
            WebVideoInfoLoader.RequestParams reqParams = new WebVideoInfoLoader.RequestParams();
            reqParams.category = requestFilter.category;
            reqParams.sort = requestFilter.sort;
            reqParams.time = requestFilter.time;
            reqParams.page = getPagesLoaded() + 1;
            infoLoader.request(reqParams)
                    .subscribe(parsedList -> {
                        resourcesList.addAll(parsedList);
                        updateView();
                    }, throwable -> {/* IO Exception*/});
        } else {
            Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.no_more_contents), Snackbar.LENGTH_LONG).show();
        }
    }

    public RequestFilter getRequestFilter() {
        return requestFilter;
    }

    public static final class RequestFilter {
        public static final String CATEGORY_ALL = "all";
        public static final String CATEGORY_MV_SHOOTING_COMPETITION = String.valueOf(35);
        public static final String CATEGORY_SINGING_CONTEST_1516 = String.valueOf(25);
        public static final String CATEGORY_SINGING_CONTEST_1617 = String.valueOf(36);
        public static final String CATEGORY_ONE_MINUTE_ENGLISH = String.valueOf(13);
        public static final String CATEGORY_CHORAL_SPEAKING = String.valueOf(32);
        public static final String CATEGORY_GOSPEL_WEEK = String.valueOf(27);
        public static final String CATEGORY_BOOK_SHARING = String.valueOf(26);
        public static final String CATEGORY_SPORT_DAY = String.valueOf(28);
        //public static final String CATEGORY_TEACHER_SHARING = String.valueOf(29);
        //public static final String CATEGORY_ASSEMBLY = String.valueOf(34);

        public static final String SORT_ALL = "view_all";
        public static final String SORT_RECENT = "most_recent";
        public static final String SORT_MOST_VIEWED = "most_viewed";
        //public static final String SORT_FEATURED = "featured";
        public static final String SORT_TOP_RATED = "top_rated";
        //public static final String SORT_COMMENTED = "most_commented";

        public static final String TIME_ALL_TIME = "all_time";
        public static final String TIME_TODAY = "today";
        public static final String TIME_YESTERDAY = "yesterday";
        public static final String TIME_THIS_WEEK = "this_week";
        public static final String TIME_LAST_WEEK = "last_week";
        public static final String TIME_THIS_MONTH = "this_month";
        public static final String TIME_LAST_MONTH = "last_month";
        public static final String TIME_THIS_YEAR = "this_year";
        public static final String TIME_LAST_YEAR = "last_year";

        public String category = "all";
        public String sort = "view_all";
        public String time = "all_time";

        public final TwoWayMap<String, String> categoryMap = new TwoWayMap<>();
        public final TwoWayMap<String, String> sortMap = new TwoWayMap<>();
        public final TwoWayMap<String, String> timeMap = new TwoWayMap<>();

        public RequestFilter(Resources res) {
            categoryMap.put(CATEGORY_ALL, res.getString(R.string.filter_all));
            categoryMap.put(CATEGORY_MV_SHOOTING_COMPETITION, res.getString(R.string.filter_category_mv_shooting));
            categoryMap.put(CATEGORY_SINGING_CONTEST_1516, res.getString(R.string.filter_category_singing_contest, "15-16"));
            categoryMap.put(CATEGORY_SINGING_CONTEST_1617, res.getString(R.string.filter_category_singing_contest, "16-17"));
            categoryMap.put(CATEGORY_ONE_MINUTE_ENGLISH, res.getString(R.string.filter_category_one_min_english));
            categoryMap.put(CATEGORY_CHORAL_SPEAKING, res.getString(R.string.filter_category_choral_speaking));
            categoryMap.put(CATEGORY_GOSPEL_WEEK, res.getString(R.string.filter_category_gospel_week));
            categoryMap.put(CATEGORY_BOOK_SHARING, res.getString(R.string.filter_category_book_sharing));
            categoryMap.put(CATEGORY_SPORT_DAY, res.getString(R.string.filter_category_sport_day));

            sortMap.put(SORT_ALL, res.getString(R.string.filter_all));
            sortMap.put(SORT_MOST_VIEWED, res.getString(R.string.filter_sort_most_viewed));
            sortMap.put(SORT_RECENT, res.getString(R.string.filter_sort_recent));
            sortMap.put(SORT_TOP_RATED, res.getString(R.string.filter_top_rated));

            timeMap.put(TIME_ALL_TIME, res.getString(R.string.filter_all));
            timeMap.put(TIME_TODAY, res.getString(R.string.filter_time_today));
            timeMap.put(TIME_YESTERDAY, res.getString(R.string.filter_time_yesterday));
            timeMap.put(TIME_THIS_WEEK, res.getString(R.string.filter_time_this_week));
            timeMap.put(TIME_LAST_WEEK, res.getString(R.string.filter_time_last_week));
            timeMap.put(TIME_THIS_MONTH, res.getString(R.string.filter_time_this_month));
            timeMap.put(TIME_LAST_MONTH, res.getString(R.string.filter_time_last_month));
            timeMap.put(TIME_THIS_YEAR, res.getString(R.string.filter_time_this_year));
            timeMap.put(TIME_LAST_YEAR, res.getString(R.string.filter_time_last_year));
        }
    }
}
