package com.persoff68.fatodo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractModel {

    private String email;

    private String username;

    private Set<String> authorities;

    private String password;

    private String provider;

    private String providerId;

    private boolean activated;

    private Info info;

    private Settings settings;

    private Notifications notifications;

    @Data
    public static class Info {

        private String firstname;

        private String lastname;

        private String gender;

        private String imageFilename;

    }

    @Data
    public static class Settings {

        private String language;

        private String timezone;

        private String timeFormat;

        private String dateFormat;
    }

    @Data
    public static class Notifications {
        private Set<PushNotificationType> pushNotifications;
        private Set<EmailNotificationType> emailNotifications;

    }

    public enum PushNotificationType {
        ITEM_CREATE, ITEM_GROUP_CREATE, ITEM_MEMBER_ADD,
        CHAT_CREATE, CHAT_MESSAGE_CREATE, CHAT_REACTION_INCOMING,
        CONTACT_REQUEST_INCOMING, CONTACT_ACCEPT_OUTCOMING,
        COMMENT_CREATE, COMMENT_REACTION_INCOMING,
        REMINDER
    }

    public enum EmailNotificationType {
        REMINDER
    }

}
