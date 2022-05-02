package com.persoff68.fatodo.service.exception;

import com.persoff68.fatodo.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class ReminderException extends AbstractException {
    private static final String MESSAGE = "Wrong reminder params";

    public ReminderException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE);
    }

}
