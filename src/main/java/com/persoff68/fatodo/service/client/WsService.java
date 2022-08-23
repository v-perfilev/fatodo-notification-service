package com.persoff68.fatodo.service.client;

import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.mapper.ReminderMapper;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.ReminderThread;
import com.persoff68.fatodo.model.constant.WsEventType;
import com.persoff68.fatodo.model.dto.ReminderDTO;
import com.persoff68.fatodo.model.dto.WsEventDTO;
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

    public void sendReminderEvent(Reminder reminder) {
        List<UUID> userIdList = permissionService.getThreadUserIds(reminder.getThread());
        ReminderDTO reminderDTO = reminderMapper.pojoToDTO(reminder);
        WsEventDTO dto = new WsEventDTO(userIdList, WsEventType.REMINDER, reminderDTO);
        wsServiceClient.sendEvent(dto);
    }

}
