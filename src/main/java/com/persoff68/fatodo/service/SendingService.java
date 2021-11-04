package com.persoff68.fatodo.service;

import com.persoff68.fatodo.client.ItemServiceClient;
import com.persoff68.fatodo.client.MailServiceClient;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.model.Notification;
import com.persoff68.fatodo.model.NotificationMail;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.ReminderMessage;
import com.persoff68.fatodo.model.ReminderThread;
import com.persoff68.fatodo.model.UserInfo;
import com.persoff68.fatodo.model.constant.ReminderThreadType;
import com.persoff68.fatodo.repository.ReminderRepository;
import com.persoff68.fatodo.repository.ReminderThreadRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SendingService {

    private final ReminderThreadRepository threadRepository;
    private final ReminderRepository reminderRepository;
    private final ItemServiceClient itemServiceClient;
    private final UserServiceClient userServiceClient;
    private final MailServiceClient mailServiceClient;

    public void sendNotification(Notification notification) {
        ReminderThread thread = getThread(notification);
        ReminderMessage message = getReminderMessageByThread(thread);
        List<UserInfo> userList = getUserListByIds(message.getUserIds());
        sendMailNotifications(message, userList);
    }

    private void sendMailNotifications(ReminderMessage message, List<UserInfo> userList) {
        userList.stream()
                .map(u -> new NotificationMail(u, message))
                .forEach(mailServiceClient::sendNotification);
    }

    private List<UserInfo> getUserListByIds(List<UUID> userIdList) {
        return userServiceClient.getAllInfoByIds(userIdList);
    }

    private ReminderMessage getReminderMessageByThread(ReminderThread thread) {
        UUID targetId = thread.getTargetId();
        ReminderThreadType type = thread.getType();
        return switch (type) {
            case ITEM -> itemServiceClient.getReminderByItemId(targetId);
        };
    }

    private ReminderThread getThread(Notification notification) {
        UUID reminderId = notification.getId();
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(ModelNotFoundException::new);
        UUID threadId = reminder.getThreadId();
        return threadRepository.findById(threadId)
                .orElseThrow(ModelNotFoundException::new);
    }

}
