package com.minhnhat.demo.features.profile.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileSettingsRequest {
    private String fullName;
    private String avatarUrl;
    private Boolean autoInvestEnabled;
    private String investmentStyle;
    private Integer autoInvestDayOfMonth;
}

