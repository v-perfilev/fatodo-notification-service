package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.DateParams;
import com.persoff68.fatodo.model.constant.Periodicity;
import com.persoff68.fatodo.model.dto.ReminderDTO;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

public class TestReminderDTO extends ReminderDTO {

    @Builder
    TestReminderDTO(UUID id,
                    Periodicity periodicity,
                    DateParams date,
                    List<Integer> weekDays,
                    List<Integer> monthDays) {
        super();
        setId(id);
        setPeriodicity(periodicity);
        setDate(date);
        setWeekDays(weekDays);
        setMonthDays(monthDays);
    }

    public static TestReminderDTOBuilder defaultBuilder() {
        DateParams date = TestDateParams.defaultBuilder().build().toParent();
        return TestReminderDTO.builder()
                .periodicity(Periodicity.ONCE)
                .date(date);
    }

    public ReminderDTO toParent() {
        ReminderDTO dto = new ReminderDTO();
        dto.setId(getId());
        dto.setPeriodicity(getPeriodicity());
        dto.setDate(getDate());
        dto.setWeekDays(getWeekDays());
        dto.setMonthDays(getMonthDays());
        return dto;
    }

}
