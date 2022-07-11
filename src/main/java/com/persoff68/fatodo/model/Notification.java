package com.persoff68.fatodo.model;

import com.persoff68.fatodo.model.constant.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "ftd_reminder_notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"reminder"})
public class Notification extends AbstractModel {

    @ManyToOne
    private Reminder reminder;

    @Temporal(TemporalType.TIMESTAMP)
    private Instant date;

    @NotNull
    private NotificationStatus status = NotificationStatus.CREATED;

    public Notification(Reminder reminder, Instant date) {
        this.reminder = reminder;
        this.date = date;
    }

}
