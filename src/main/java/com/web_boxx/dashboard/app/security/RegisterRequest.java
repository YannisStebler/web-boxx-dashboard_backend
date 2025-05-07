package com.web_boxx.dashboard.app.security;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String birthdate;
    private String password;
}
