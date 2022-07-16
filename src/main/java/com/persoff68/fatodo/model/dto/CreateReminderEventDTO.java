package com.persoff68.fatodo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReminderEventDTO {

    private EventType type;

    private List<UUID> recipientIds;

    private UUID groupId;

    private UUID itemId;

    public enum EventType {
        REMINDER
    }

    public static CreateReminderEventDTO reminder(List<UUID> recipientIds, UUID groupId, UUID itemId) {
        CreateReminderEventDTO dto = new CreateReminderEventDTO();
        dto.setType(EventType.REMINDER);
        dto.setRecipientIds(recipientIds);
        dto.setGroupId(groupId);
        dto.setItemId(itemId);
        return dto;
    }

}
