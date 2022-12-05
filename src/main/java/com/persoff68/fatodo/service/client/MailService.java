package com.persoff68.fatodo.service.client;

import com.persoff68.fatodo.client.MailServiceClient;
import com.persoff68.fatodo.client.UserSystemServiceClient;
import com.persoff68.fatodo.model.NotificationMail;
import com.persoff68.fatodo.model.ReminderInfo;
import com.persoff68.fatodo.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Async
public class MailService {
    private final MailServiceClient mailServiceClient;
    private final UserSystemServiceClient userSystemServiceClient;

    @Transactional
    public void sendNotification(ReminderInfo reminderInfo) {
        List<User> userList = getUserListByIds(reminderInfo.getUserIds());
        List<User> filteredUserList = userList.stream()
                .filter(u -> u.getSettings().isEmailReminders())
                .toList();
        sendMailNotifications(reminderInfo, filteredUserList);
    }

    private void sendMailNotifications(ReminderInfo reminderInfo, List<User> userList) {
        userList.stream()
                .map(u -> new NotificationMail(u, reminderInfo))
                .forEach(mailServiceClient::sendNotification);
    }

    private List<User> getUserListByIds(List<UUID> userIdList) {
        return userSystemServiceClient.getAllUserDataByIds(userIdList);
    }

}
