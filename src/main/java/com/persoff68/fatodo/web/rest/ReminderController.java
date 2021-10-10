package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.dto.ReminderDTO;
import com.persoff68.fatodo.model.mapper.ReminderMapper;
import com.persoff68.fatodo.web.rest.vm.ReminderVM;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(ReminderController.ENDPOINT)
@RequiredArgsConstructor
public class ReminderController {
    static final String ENDPOINT = "/api/reminders";

    private final ReminderMapper reminderMapper;

    @GetMapping(value = "/{targetId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ReminderDTO>> getAllByTargetId(@PathVariable UUID targetId) {
        // TODO get reminders
        System.out.println(targetId);
        List<Reminder> reminderList = Collections.emptyList();
        List<ReminderDTO> dtoList = reminderList.stream()
                .map(reminderMapper::pojoToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping(value = "/{targetId}/add")
    public ResponseEntity<Void> addReminders(@PathVariable UUID targetId,
                                             @Valid @RequestBody List<ReminderVM> reminderVMList) {
        List<Reminder> reminderList = reminderVMList.stream()
                .map(reminderMapper::vmToPojo)
                .collect(Collectors.toList());
        // TODO add reminders
        System.out.println(targetId);
        System.out.println(reminderList);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(value = "/{targetId}/update")
    public ResponseEntity<Void> updateReminders(@PathVariable UUID targetId,
                                                @Valid @RequestBody List<ReminderVM> reminderVMList) {
        List<Reminder> reminderList = reminderVMList.stream()
                .map(reminderMapper::vmToPojo)
                .collect(Collectors.toList());
        // TODO update reminders
        System.out.println(targetId);
        System.out.println(reminderList);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/update")
    public ResponseEntity<Void> deleteReminders(@RequestBody List<UUID> reminderIdList) {
        // TODO delete reminders
        System.out.println(reminderIdList);
        return ResponseEntity.ok().build();
    }

}
