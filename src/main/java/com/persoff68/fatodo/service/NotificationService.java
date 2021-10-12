package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Notification;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void generateNotifications(Reminder reminder) {

    }

    public void deleteNotifications(Reminder reminder) {
        List<Notification> notificationList = notificationRepository.findAllByReminderId(reminder.getId());
        notificationRepository.deleteAll(notificationList);
    }

    public Notification createOnceNotification(Reminder reminder) {
        Notification notification = new Notification();
        notification.setReminderId(reminder.getId());
        // TODO create
        return notification;
    }

}
