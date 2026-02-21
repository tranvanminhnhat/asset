package com.minhnhat.demo.dto.response;

import lombok.Builder;

@Builder
public class RegisterAccountResponse {
    private String userId;

    private String email;

    private String status;
}
