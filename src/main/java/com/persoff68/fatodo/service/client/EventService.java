package com.persoff68.fatodo.service.client;

import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.mapper.ReminderMapper;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.constant.EventType;
import com.persoff68.fatodo.model.dto.EventDTO;
import com.persoff68.fatodo.model.dto.ReminderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventServiceClient eventServiceClient;
    private final PermissionService permissionService;
    private final ReminderMapper reminderMapper;

    public void sendReminderEvent(Reminder reminder) {
        List<UUID> userIdList = permissionService.getThreadUserIds(reminder.getThread());
        ReminderDTO reminderDTO = reminderMapper.pojoToDTO(reminder);
        EventDTO dto = new EventDTO(userIdList, EventType.REMINDER, reminderDTO);
        eventServiceClient.addEvent(dto);
    }

}
