package com.persoff68.fatodo.service.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.mapper.ReminderMapper;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.constant.WsEventType;
import com.persoff68.fatodo.model.dto.ReminderDTO;
import com.persoff68.fatodo.model.dto.event.WsEventDTO;
import com.persoff68.fatodo.service.exception.ModelInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WsService {

    private final WsServiceClient wsServiceClient;
    private final PermissionService permissionService;
    private final ReminderMapper reminderMapper;
    private final ObjectMapper objectMapper;

    public void sendReminderEvent(Reminder reminder) {
        List<UUID> userIdList = permissionService.getThreadUserIds(reminder.getThread());
        ReminderDTO reminderDTO = reminderMapper.pojoToDTO(reminder);
        String payload = serialize(reminderDTO);
        WsEventDTO dto = new WsEventDTO(userIdList, WsEventType.REMINDER, payload);
        wsServiceClient.sendEvent(dto);
    }

    private String serialize(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new ModelInvalidException();
        }
    }

}
