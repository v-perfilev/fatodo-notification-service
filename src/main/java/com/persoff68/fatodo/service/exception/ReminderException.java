package com.persoff68.fatodo.service.exception;

public class ReminderException extends RuntimeException {
    private static final String MESSAGE = "Wrong reminder params";

    public ReminderException() {
        super(MESSAGE);
    }

}
