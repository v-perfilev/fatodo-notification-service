package com.persoff68.fatodo.model;

import com.persoff68.fatodo.model.constant.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Document(collection = "ftd_notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Notification extends AbstractModel {

    @NotNull
    @Indexed
    private UUID reminderId;

    @NotNull
    private Instant date;

    @NotNull
    private NotificationStatus status = NotificationStatus.CREATED;

    public Notification(UUID reminderId, Instant date) {
        this.reminderId = reminderId;
        this.date = date;
    }

}
