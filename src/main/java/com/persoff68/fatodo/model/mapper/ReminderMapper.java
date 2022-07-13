package com.persoff68.fatodo.model.mapper;

import com.persoff68.fatodo.model.CalendarReminder;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.dto.CalendarReminderDTO;
import com.persoff68.fatodo.model.dto.ReminderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReminderMapper {

    ReminderDTO pojoToDTO(Reminder reminder);

    @Mapping(source = "thread.parentId", target ="parentId")
    @Mapping(source = "thread.targetId", target ="targetId")
    @Mapping(source = "thread.type", target ="type")
    @Mapping(source = "reminder.periodicity", target ="periodicity")
    CalendarReminderDTO calendarPojoToDTO(CalendarReminder calendarReminder);

    Reminder dtoToPojo(ReminderDTO reminderDTO);

}
