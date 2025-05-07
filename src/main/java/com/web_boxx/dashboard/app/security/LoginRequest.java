package com.web_boxx.dashboard.app.security;

import lombok.Data;

@Data
public class LoginRequest {
    private String identifier;
    private String password;
}