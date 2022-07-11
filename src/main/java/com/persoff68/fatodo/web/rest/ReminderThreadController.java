package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.service.ReminderThreadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(ReminderThreadController.ENDPOINT)
@RequiredArgsConstructor
@Transactional
public class ReminderThreadController {
    static final String ENDPOINT = "/api/threads";

    private final ReminderThreadService threadService;

    @DeleteMapping("/{parentId}/parent")
    public ResponseEntity<Void> deleteAllByParentId(@PathVariable UUID parentId) {
        threadService.deleteByParentId(parentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{targetId}/target")
    public ResponseEntity<Void> deleteByTargetId(@PathVariable UUID targetId) {
        threadService.deleteByTargetId(targetId);
        return ResponseEntity.ok().build();
    }

}
