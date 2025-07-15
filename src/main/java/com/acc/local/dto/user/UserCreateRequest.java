package com.acc.local.dto.user;

import lombok.Data;

@Data
public class UserCreateRequest {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private Boolean enabled = true;
    private Boolean temporary = false;
}