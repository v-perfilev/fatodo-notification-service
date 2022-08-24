package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.constant.WsEventType;
import com.persoff68.fatodo.model.dto.event.WsEventDTO;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

public class TestWsEventDTO extends WsEventDTO {

    @Builder
    TestWsEventDTO(List<UUID> userIdList, WsEventType type, String payload) {
        super(userIdList, type, payload);
    }

    public static TestWsEventDTOBuilder defaultBuilder() {
        return TestWsEventDTO.builder()
                .userIdList(List.of(UUID.randomUUID()))
                .type(WsEventType.REMINDER);
    }

    public WsEventDTO toParent() {
        WsEventDTO dto = new WsEventDTO();
        dto.setUserIds(getUserIds());
        dto.setType(getType());
        dto.setPayload(getPayload());
        dto.setUserId(getUserId());
        dto.setDate(getDate());
        return dto;
    }

}
