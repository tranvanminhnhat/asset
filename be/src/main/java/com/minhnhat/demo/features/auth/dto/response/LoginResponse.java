package com.minhnhat.demo.features.auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginResponse {
    private String userId;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String status;
    private Boolean autoInvestEnabled;
    private String investmentStyle;
    private Integer autoInvestDayOfMonth;
    private String flowMode;
    private String accessToken;
    private String refreshToken;
}
