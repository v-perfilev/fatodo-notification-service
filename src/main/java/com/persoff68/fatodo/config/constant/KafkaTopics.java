package com.persoff68.fatodo.config.constant;

import lombok.Getter;

public enum KafkaTopics {
    EVENT("event"),
    MAIL_NOTIFICATION("mail_notification"),
    WS("ws");

    @Getter
    private final String value;

    KafkaTopics(String value) {
        this.value = value;
    }

}
