package com.app.ecom_application.Dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String message;
    private UserResponse user;
}
