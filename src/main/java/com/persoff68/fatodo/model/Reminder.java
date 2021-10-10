package com.persoff68.fatodo.model;

import com.persoff68.fatodo.model.constant.Periodicity;
import com.persoff68.fatodo.model.constant.ReminderType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Document(collection = "ftd_reminder")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Reminder extends AbstractModel {

    @NotNull
    @Indexed
    private UUID targetId;

    @NotNull
    ReminderType type;

    private Periodicity periodicity;
    private DateParams date;
    private List<Integer> weekDays;
    private List<Integer> monthDays;
}
