package com.persoff68.fatodo.model.dto;

import com.persoff68.fatodo.config.constant.AppConstants;
import com.persoff68.fatodo.model.constant.Periodicity;
import com.persoff68.fatodo.model.constant.ReminderThreadType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
public class CalendarReminderDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = AppConstants.SERIAL_VERSION_UID;

    private UUID parentId;
    private UUID targetId;
    private ReminderThreadType type;
    private Periodicity periodicity;
    private Date date;
}
