package com.minhnhat.demo.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginResponse {
    private String userId;
    private String email;
    private String status;
    private String accessToken;
}
