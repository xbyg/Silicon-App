package com.xbyg_plus.silicon.fragment.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.data.repository.NotificationRepository;
import com.xbyg_plus.silicon.model.Notification;

import java.text.SimpleDateFormat;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class NotificationRVAdapter extends RecyclerView.Adapter<NotificationRVAdapter.ViewHolder> {
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView content;
        TextView releaseTime;
        ImageView delete;

        ViewHolder(View root) {
            super(root);
            title = root.findViewById(R.id.title);
            content = root.findViewById(R.id.content);
            releaseTime = root.findViewById(R.id.release_time);
            delete = root.findViewById(R.id.delete);
        }
    }

    private List<Notification> notificationList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm MM/dd/yyyy");

    public NotificationRVAdapter(Single<List<Notification>> dataSource) {
        dataSource.observeOn(AndroidSchedulers.mainThread())
                .subscribe(notifications -> {
                    this.notificationList = notifications;
                    notifyDataSetChanged();
                });
    }

    @Override
    public NotificationRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(NotificationRVAdapter.ViewHolder holder, int position) {
        Notification notification = notificationList.get(position);

        holder.title.setText(notification.getTitle());
        holder.content.setText(notification.getMessage());
        holder.releaseTime.setText(dateFormat.format(notification.getDate()));
        holder.delete.setOnClickListener(v -> {
            NotificationRepository.instance.deleteSingle(notification)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::notifyDataSetChanged);
        });
    }

    @Override
    public int getItemCount() {
        return notificationList == null ? 0 : notificationList.size();
    }
}
