package com.minhnhat.demo.features.auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RefreshTokenResponse {
    private String accessToken;
    private String refreshToken;
}

