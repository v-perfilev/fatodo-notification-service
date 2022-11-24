package com.persoff68.fatodo.service.client;

import com.persoff68.fatodo.client.MailServiceClient;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.model.NotificationMail;
import com.persoff68.fatodo.model.ReminderInfo;
import com.persoff68.fatodo.model.UserInfo;
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
    private final UserServiceClient userServiceClient;

    @Transactional
    public void sendNotification(ReminderInfo reminderInfo) {
        List<UserInfo> userList = getUserListByIds(reminderInfo.getUserIds());
        sendMailNotifications(reminderInfo, userList);
    }

    private void sendMailNotifications(ReminderInfo reminderInfo, List<UserInfo> userList) {
        userList.stream()
                .map(u -> new NotificationMail(u, reminderInfo))
                .forEach(mailServiceClient::sendNotification);
    }

    private List<UserInfo> getUserListByIds(List<UUID> userIdList) {
        return userServiceClient.getAllInfoByIds(userIdList);
    }

}
