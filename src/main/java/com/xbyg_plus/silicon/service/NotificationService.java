package com.xbyg_plus.silicon.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.activity.MainActivity;
import com.xbyg_plus.silicon.data.repository.NotificationRepository;
import com.xbyg_plus.silicon.model.Notification;
import com.xbyg_plus.silicon.model.SchoolAccount;
import com.xbyg_plus.silicon.utils.SchoolAccountHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

public class NotificationService extends FirebaseMessagingService {
    public static final String ACTION_VIEW_NOTIFICATION = "ACTION_VIEW_NOTIFICATION";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Notification notification = handleMessage(remoteMessage);
        NotificationRepository.instance.insertSingle(notification)
                .subscribe(() -> EventBus.getDefault().post(new NotificationReceivedEvent(notification)));

        notifyStatusBar(notification);
    }

    private Notification handleMessage(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();

        SchoolAccount account = SchoolAccountHelper.getInstance().getSchoolAccount();
        String title = data.get("title").replace("[name]", account.getName()).replace("[class]", account.getClassRoom()).replace("[no]", String.valueOf(account.getClassNo())).replace("[sid]", account.getId());
        String msg = data.get("msg").replace("[name]", account.getName()).replace("[class]", account.getClassRoom()).replace("[no]", String.valueOf(account.getClassNo())).replace("[sid]", account.getId());

        return new Notification(title, msg, Long.parseLong(data.get("date")));
    }

    private void notifyStatusBar(Notification notification) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getMessage());

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setAction(ACTION_VIEW_NOTIFICATION);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(0, builder.build());
    }

    public class NotificationReceivedEvent {
        private Notification notification;

        private NotificationReceivedEvent(Notification notification) {
            this.notification = notification;
        }

        public Notification getNotification() {
            return notification;
        }
    }

    public static class BootReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                context.startService(new Intent(context, NotificationService.class));
            }
        }
    }
}
