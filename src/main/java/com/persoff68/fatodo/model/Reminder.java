package com.persoff68.fatodo.model;

import com.persoff68.fatodo.model.constant.Periodicity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ftd_reminder")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"thread"})
public class Reminder extends AbstractModel {

    @ManyToOne
    private ReminderThread thread;

    private Periodicity periodicity;
    private DateParams date;
    private List<Integer> weekDays;
    private List<Integer> monthDays;

    @Temporal(TemporalType.TIMESTAMP)
    private Instant lastNotificationDate;

    private boolean locked;

    @OneToMany(mappedBy = "reminder", cascade = {CascadeType.ALL})
    private List<Notification> notifications;

}
