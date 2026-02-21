package com.minhnhat.demo.dto.request;

import lombok.Data;

@Data
public class RegisterAccountRequest {

    private String email;

    private String password;

    private String confirmPassword;
}
