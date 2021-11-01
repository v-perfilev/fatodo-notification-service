package com.persoff68.fatodo.model;

import com.persoff68.fatodo.model.constant.Periodicity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.Instant;
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
    private UUID threadId;

    private Periodicity periodicity;
    private DateParams date;
    private List<Integer> weekDays;
    private List<Integer> monthDays;

    @Indexed
    private Instant lastNotificationDate;
    @Indexed
    private boolean locked = false;

}
