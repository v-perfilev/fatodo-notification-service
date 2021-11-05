package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.NotificationMail;
import lombok.Builder;

public class TestNotificationMail extends NotificationMail {
    private static final String DEFAULT_VALUE = "test_value";
    private static final String DEFAULT_MAIL = "test@test.test";

    @Builder
    TestNotificationMail(
            String language,
            String email,
            String username,
            String message,
            String url) {
        super();
        setLanguage(language);
        setEmail(email);
        setUsername(username);
        setMessage(message);
        setUrl(url);
    }

    public static TestNotificationMailBuilder defaultBuilder() {
        return TestNotificationMail.builder()
                .language("en")
                .email(DEFAULT_MAIL)
                .username(DEFAULT_VALUE)
                .message(DEFAULT_VALUE)
                .url(DEFAULT_VALUE);
    }

}
