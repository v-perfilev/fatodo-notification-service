package com.persoff68.fatodo.web.rest;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
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

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@RestController
@RequestMapping(ReminderController.ENDPOINT)
@Validated
@RequiredArgsConstructor
public class ReminderController {
    static final String ENDPOINT = "/api/reminder";

    private final ReminderService reminderService;
    private final ReminderMapper reminderMapper;

    @GetMapping(value = "/calendar")
    public ResponseEntity<Multimap<String, CalendarReminderDTO>> getAllByMonths(
            @RequestParam("yearFrom") @YearConstraint Integer yearFrom,
            @RequestParam("monthFrom") @MonthConstraint Integer monthFrom,
            @RequestParam("yearTo") @YearConstraint Integer yearTo,
            @RequestParam("monthTo") @MonthConstraint Integer monthTo,
            @RequestParam("timezone") @TimezoneConstraint String timezone
    ) {
        List<CalendarReminder> calendarReminderList = reminderService.getAllCalendarRemindersByMonths(
                yearFrom,
                monthFrom,
                yearTo,
                monthTo,
                timezone
        );

        Multimap<String, CalendarReminderDTO> dtoList = calendarReminderList.stream()
                .map(reminderMapper::calendarPojoToDTO)
                .collect(Multimaps.toMultimap(
                        r -> buildMonthKey(r, timezone),
                        Function.identity(),
                        HashMultimap::create
                ));

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

    private static String buildMonthKey(CalendarReminderDTO calendarReminderDTO, String timezone) {
        Instant instant = calendarReminderDTO.getDate().toInstant().truncatedTo(ChronoUnit.SECONDS);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.of(timezone));
        Calendar calendar = GregorianCalendar.from(zonedDateTime);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        return year + "_" + month;
    }

}
