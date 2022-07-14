package com.persoff68.fatodo.service.client;

import com.persoff68.fatodo.client.ItemServiceClient;
import com.persoff68.fatodo.client.MailServiceClient;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.model.Notification;
import com.persoff68.fatodo.model.NotificationMail;
import com.persoff68.fatodo.model.ReminderMailInfo;
import com.persoff68.fatodo.model.ReminderThread;
import com.persoff68.fatodo.model.UserInfo;
import com.persoff68.fatodo.model.constant.ReminderThreadType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailService {

    private final ItemServiceClient itemServiceClient;
    private final MailServiceClient mailServiceClient;
    private final UserServiceClient userServiceClient;

    @Transactional
    public void sendNotification(Notification notification) {
        ReminderThread thread = notification.getReminder().getThread();
        ReminderMailInfo message = getReminderMessageByThread(thread);
        List<UserInfo> userList = getUserListByIds(message.getUserIds());
        sendMailNotifications(message, userList);
    }

    private void sendMailNotifications(ReminderMailInfo message, List<UserInfo> userList) {
        userList.stream()
                .map(u -> new NotificationMail(u, message))
                .forEach(mailServiceClient::sendNotification);
    }

    private List<UserInfo> getUserListByIds(List<UUID> userIdList) {
        return userServiceClient.getAllInfoByIds(userIdList);
    }

    private ReminderMailInfo getReminderMessageByThread(ReminderThread thread) {
        UUID targetId = thread.getTargetId();
        ReminderThreadType type = thread.getType();
        return switch (type) {
            case ITEM -> itemServiceClient.getReminderMailInfo(targetId);
        };
    }

}
