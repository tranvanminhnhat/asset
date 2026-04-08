package com.minhnhat.demo.features.auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegisterAccountResponse {
    private String userId;

    private String email;

    private String status;
}
