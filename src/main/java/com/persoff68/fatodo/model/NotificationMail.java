package com.persoff68.fatodo.model;

import com.persoff68.fatodo.config.constant.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMail implements Serializable {
    @Serial
    private static final long serialVersionUID = AppConstants.SERIAL_VERSION_UID;

    private String language;
    private String email;
    private String username;
    private String message;
    private String url;

    public NotificationMail(UserInfo user, ReminderMailInfo message) {
        this.language = user.getLanguage();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.message = message.getMessage();
        this.url = message.getUrl();
    }

}
