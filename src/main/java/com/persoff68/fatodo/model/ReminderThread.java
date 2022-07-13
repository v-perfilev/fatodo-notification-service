package com.persoff68.fatodo.model;

import com.persoff68.fatodo.model.constant.ReminderThreadType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ftd_reminder_thread")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"reminders"})
public class ReminderThread extends AbstractModel {

    @NotNull
    private UUID parentId;

    @NotNull
    private UUID targetId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ReminderThreadType type;

    @OneToMany(mappedBy = "thread", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reminder> reminders = new ArrayList<>();

    public static ReminderThread of(UUID parentId, UUID targetId, ReminderThreadType type) {
        ReminderThread thread = new ReminderThread();
        thread.setParentId(parentId);
        thread.setTargetId(targetId);
        thread.setType(type);
        return thread;
    }

}
