package com.xbyg_plus.silicon.service;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.xbyg_plus.silicon.data.repository.NotificationRepository;
import com.xbyg_plus.silicon.model.Notification;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

public class NotificationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();

        Notification notification = new Notification(data.get("title"), data.get("msg"), Long.parseLong(data.get("date")));
        NotificationRepository.instance.insertSingle(notification)
                .subscribe(() -> EventBus.getDefault().post(new NotificationReceivedEvent(notification)));
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
}
