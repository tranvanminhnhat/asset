package com.minhnhat.demo.features.auth.dto.request;

import lombok.Data;

@Data
public class RegisterAccountRequest {

    private String email;

    private String password;

    private String confirmPassword;
}
