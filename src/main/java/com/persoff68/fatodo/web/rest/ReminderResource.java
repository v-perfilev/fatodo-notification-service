package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.dto.ReminderDTO;
import com.persoff68.fatodo.model.mapper.ReminderMapper;
import com.persoff68.fatodo.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(ReminderResource.ENDPOINT)
@RequiredArgsConstructor
public class ReminderResource {
    static final String ENDPOINT = "/api/reminders";

    private final ReminderService reminderService;
    private final ReminderMapper reminderMapper;

    @GetMapping(value = "/{targetId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ReminderDTO>> getAllByTargetId(@PathVariable UUID targetId) {
        List<Reminder> reminderList = reminderService.getAllByTargetId(targetId);
        List<ReminderDTO> dtoList = reminderList.stream()
                .map(reminderMapper::pojoToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @PutMapping(value = "/{targetId}")
    public ResponseEntity<Void> setReminders(@PathVariable UUID targetId,
                                             @Valid @RequestBody List<ReminderDTO> reminderDTOList) {
        List<Reminder> reminderList = reminderDTOList.stream()
                .map(reminderMapper::dtoToPojo)
                .collect(Collectors.toList());
        reminderService.setReminders(targetId, reminderList);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping(value = "/{targetId}")
    public ResponseEntity<Void> deleteReminders(@PathVariable UUID targetId) {
        reminderService.deleteReminders(targetId);
        return ResponseEntity.ok().build();
    }

}
