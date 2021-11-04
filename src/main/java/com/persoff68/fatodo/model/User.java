package com.persoff68.fatodo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractModel {

    private String email;
    private String username;
    private String firstname;
    private String lastname;
    private String language;

}
