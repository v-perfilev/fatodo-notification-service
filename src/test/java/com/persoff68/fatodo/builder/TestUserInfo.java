package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.UserInfo;
import lombok.Builder;

public class TestUserInfo extends UserInfo {
    private static final String DEFAULT_VALUE = "test";

    @Builder
    TestUserInfo(String email,
                 String username,
                 String firstname,
                 String lastname,
                 String language) {
        setEmail(email);
        setUsername(username);
        setFirstname(firstname);
        setLastname(lastname);
        setLanguage(language);
    }

    public static TestUserInfoBuilder defaultBuilder() {
        return TestUserInfo.builder()
                .email(DEFAULT_VALUE)
                .username(DEFAULT_VALUE)
                .firstname(DEFAULT_VALUE)
                .lastname(DEFAULT_VALUE)
                .language("en");
    }

}
