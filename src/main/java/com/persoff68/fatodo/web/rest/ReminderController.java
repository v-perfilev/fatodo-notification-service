package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.mapper.ReminderMapper;
import com.persoff68.fatodo.model.CalendarReminder;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.dto.CalendarReminderDTO;
import com.persoff68.fatodo.model.dto.ReminderDTO;
import com.persoff68.fatodo.service.ReminderService;
import com.persoff68.fatodo.web.rest.validator.MonthConstraint;
import com.persoff68.fatodo.web.rest.validator.TimezoneConstraint;
import com.persoff68.fatodo.web.rest.validator.YearConstraint;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ReminderController.ENDPOINT)
@Validated
@RequiredArgsConstructor
public class ReminderController {
    static final String ENDPOINT = "/api/reminder";

    private final ReminderService reminderService;
    private final ReminderMapper reminderMapper;

    @GetMapping(value = "/calendar")
    public ResponseEntity<List<CalendarReminderDTO>> getAllByMonth(
            @RequestParam("year") @YearConstraint Integer year,
            @RequestParam("month") @MonthConstraint Integer month,
            @RequestParam("timezone") @TimezoneConstraint String timezone
    ) {
        List<CalendarReminder> calendarReminderList = reminderService.getAllCalendarRemindersByMonth(year, month,
                timezone);
        List<CalendarReminderDTO> dtoList = calendarReminderList.stream()
                .map(reminderMapper::calendarPojoToDTO)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping(value = "/{targetId}")
    public ResponseEntity<List<ReminderDTO>> getAllByTargetId(@PathVariable UUID targetId) {
        List<Reminder> reminderList = reminderService.getAllByTargetId(targetId);
        List<ReminderDTO> dtoList = reminderList.stream()
                .map(reminderMapper::pojoToDTO)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @PutMapping(value = "/{targetId}")
    public ResponseEntity<Void> setReminders(@PathVariable UUID targetId,
                                             @RequestBody List<ReminderDTO> reminderDTOList) {
        List<Reminder> reminderList = reminderDTOList.stream()
                .map(reminderMapper::dtoToPojo)
                .toList();
        reminderService.setReminders(targetId, reminderList);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
