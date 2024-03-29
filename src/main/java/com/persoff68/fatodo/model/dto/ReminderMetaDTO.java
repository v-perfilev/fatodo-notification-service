package com.persoff68.fatodo.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class ReminderMetaDTO {

    private UUID groupId;

    private UUID itemId;

}
