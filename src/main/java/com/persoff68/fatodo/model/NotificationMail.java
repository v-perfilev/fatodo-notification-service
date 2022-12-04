package com.persoff68.fatodo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMail {

    private String language;

    private String email;

    private String username;

    private String message;

    private String url;

    public NotificationMail(User user, ReminderInfo message) {
        this.language = user.getInfo().getLanguage();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.message = message.getMessage();
        this.url = message.getUrl();
    }

}
