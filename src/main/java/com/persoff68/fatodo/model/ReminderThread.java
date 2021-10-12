package com.persoff68.fatodo.model;

import com.persoff68.fatodo.model.constant.ReminderThreadType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Document(collection = "ftd_reminder_thread")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ReminderThread extends AbstractModel {

    @NotNull
    @Indexed
    private UUID targetId;

    @NotNull
    ReminderThreadType type;

    public static ReminderThread of(UUID targetId, ReminderThreadType type) {
        ReminderThread thread = new ReminderThread();
        thread.setTargetId(targetId);
        thread.setType(type);
        return thread;
    }

}
