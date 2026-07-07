package com.minhnhat.demo.features.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSettingsResponse {
    private String userId;
    private String email;
    private String fullName;
    private String avatarUrl;
    private Boolean autoInvestEnabled;
    private String investmentStyle;
    private Integer autoInvestDayOfMonth;
    private String flowMode;
    private LocalDate nextAutoInvestAt;
    private Boolean autoInvestDueToday;
}

