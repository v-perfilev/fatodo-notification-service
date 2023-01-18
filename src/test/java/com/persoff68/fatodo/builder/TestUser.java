package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.User;
import lombok.Builder;

import java.util.HashSet;
import java.util.UUID;

public class TestUser extends User {
    private static final String DEFAULT_VALUE = "test";

    @Builder
    TestUser(UUID id,
             String email,
             String username,
             String firstname,
             String lastname,
             String language,
             boolean emailReminders) {
        super();
        setId(id);
        setEmail(email);
        setUsername(username);
        setInfo(new Info());
        getInfo().setFirstname(firstname);
        getInfo().setLastname(lastname);
        setSettings(new Settings());
        getSettings().setLanguage(language);
        setNotifications(new Notifications());
        getNotifications().setPushNotifications(new HashSet<>());
        getNotifications().setEmailNotifications(new HashSet<>());
        if (emailReminders) {
            getNotifications().getEmailNotifications().add(EmailNotificationType.REMINDER);
        }
    }

    public static TestUserBuilder defaultBuilder() {
        return TestUser.builder()
                .id(UUID.randomUUID())
                .email(DEFAULT_VALUE)
                .username(DEFAULT_VALUE)
                .firstname(DEFAULT_VALUE)
                .lastname(DEFAULT_VALUE)
                .language("EN")
                .emailReminders(true);
    }

    public User toParent() {
        User user = new User();
        user.setId(getId());
        user.setEmail(getEmail());
        user.setUsername(getUsername());
        user.setInfo(new Info());
        user.getInfo().setFirstname(getInfo().getFirstname());
        user.getInfo().setLastname(getInfo().getLastname());
        user.setSettings(new Settings());
        user.getSettings().setLanguage(getSettings().getLanguage());
        user.setNotifications(getNotifications());
        return user;
    }

}
