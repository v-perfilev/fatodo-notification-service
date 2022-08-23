package com.persoff68.fatodo.model.dto;

import com.persoff68.fatodo.model.constant.WsEventType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class WsEventDTO {

    private List<UUID> userIds;

    private WsEventType type;

    private Object payload;

    private UUID userId;

    private Date date;

    public WsEventDTO(List<UUID> userIds, WsEventType type, Object payload) {
        this.userIds = userIds;
        this.type = type;
        this.payload = payload;
        this.date = new Date();
    }

}
