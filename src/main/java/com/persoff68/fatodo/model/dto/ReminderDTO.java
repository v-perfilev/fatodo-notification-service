package com.persoff68.fatodo.model.dto;

import com.persoff68.fatodo.model.AbstractModel;
import com.persoff68.fatodo.model.DateParams;
import com.persoff68.fatodo.model.constant.Periodicity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ReminderDTO extends AbstractModel {
    private Periodicity periodicity;
    private DateParams date;
    private List<Integer> weekDays;
    private List<Integer> monthDays;
}
