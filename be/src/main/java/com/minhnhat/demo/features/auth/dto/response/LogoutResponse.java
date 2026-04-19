package com.minhnhat.demo.features.auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LogoutResponse {
    private String message;
}

