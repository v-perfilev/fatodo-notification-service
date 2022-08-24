package com.persoff68.fatodo.service.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.mapper.ReminderMapper;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.constant.EventType;
import com.persoff68.fatodo.model.dto.ReminderDTO;
import com.persoff68.fatodo.model.dto.event.EventDTO;
import com.persoff68.fatodo.service.exception.ModelInvalidException;
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
    private final ObjectMapper objectMapper;

    public void sendReminderEvent(Reminder reminder) {
        List<UUID> userIdList = permissionService.getThreadUserIds(reminder.getThread());
        ReminderDTO reminderDTO = reminderMapper.pojoToDTO(reminder);
        String payload = serialize(reminderDTO);
        EventDTO dto = new EventDTO(userIdList, EventType.REMINDER, payload);
        eventServiceClient.addEvent(dto);
    }

    private String serialize(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new ModelInvalidException();
        }
    }

}
