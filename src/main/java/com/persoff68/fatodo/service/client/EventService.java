package com.persoff68.fatodo.service.client;

import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.ReminderThread;
import com.persoff68.fatodo.model.dto.CreateReminderEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventServiceClient eventServiceClient;
    private final PermissionService permissionService;

    public void sendReminderEvent(Reminder reminder) {
        ReminderThread thread = reminder.getThread();
        UUID groupId = thread.getParentId();
        UUID itemId = thread.getTargetId();
        List<UUID> recipientIdList = permissionService.getThreadUserIds(thread);
        CreateReminderEventDTO dto = CreateReminderEventDTO.reminder(recipientIdList, groupId, itemId);
        eventServiceClient.addReminderEvent(dto);
    }

}
