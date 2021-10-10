package com.persoff68.fatodo.web.rest.vm;

import com.persoff68.fatodo.model.DateParams;
import com.persoff68.fatodo.model.constant.Periodicity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ReminderVM {
    private Periodicity periodicity;
    private DateParams date;
    private List<Integer> weekDays;
    private List<Integer> monthDays;
}
