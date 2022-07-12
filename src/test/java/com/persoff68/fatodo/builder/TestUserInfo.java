package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.UserInfo;
import lombok.Builder;

import java.util.UUID;

public class TestUserInfo extends UserInfo {
    private static final String DEFAULT_VALUE = "test";

    @Builder
    TestUserInfo(UUID id,
                 String email,
                 String username,
                 String firstname,
                 String lastname,
                 String language) {
        super();
        setId(id);
        setEmail(email);
        setUsername(username);
        setFirstname(firstname);
        setLastname(lastname);
        setLanguage(language);
    }

    public static TestUserInfoBuilder defaultBuilder() {
        return TestUserInfo.builder()
                .id(UUID.randomUUID())
                .email(DEFAULT_VALUE)
                .username(DEFAULT_VALUE)
                .firstname(DEFAULT_VALUE)
                .lastname(DEFAULT_VALUE)
                .language("en");
    }

    public UserInfo toParent() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(getId());
        userInfo.setEmail(getEmail());
        userInfo.setUsername(getUsername());
        userInfo.setFirstname(getFirstname());
        userInfo.setLastname(getLastname());
        userInfo.setLanguage(getLanguage());
        return userInfo;
    }

}
