package com.minhnhat.demo.features.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @Email
    @NotBlank
    @Size(min = 5, max = 255)
    private String email;

    @NotBlank
    @Size(min = 12, max = 64)
    private String password;
}
