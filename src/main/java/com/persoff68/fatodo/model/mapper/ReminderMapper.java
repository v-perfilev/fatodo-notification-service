package com.persoff68.fatodo.model.mapper;

import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.dto.ReminderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReminderMapper {

    ReminderDTO pojoToDTO(Reminder reminder);

    Reminder dtoToPojo(ReminderDTO reminderDTO);

}
