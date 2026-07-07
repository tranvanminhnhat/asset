package com.minhnhat.demo.features.profile.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.common.security.dto.CurrentUserInfo;
import com.minhnhat.demo.features.profile.dto.request.UpdateProfileSettingsRequest;
import com.minhnhat.demo.features.profile.dto.response.ProfileSettingsResponse;
import com.minhnhat.demo.features.profile.service.ProfileSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profile/settings")
@RequiredArgsConstructor
public class ProfileSettingsController {

    private final ProfileSettingsService profileSettingsService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ApiResponse<ProfileSettingsResponse> getSettings() {
        CurrentUserInfo currentUser = securityUtils.getCurrentUser();
        ProfileSettingsResponse response = profileSettingsService.getSettings(currentUser.getUserId());
        return ApiResponse.<ProfileSettingsResponse>builder()
                .data(response)
                .message("SUCCESS", "Profile settings retrieved", "ProfileService")
                .build();
    }

    @PutMapping
    public ApiResponse<ProfileSettingsResponse> updateSettings(@RequestBody UpdateProfileSettingsRequest request) {
        CurrentUserInfo currentUser = securityUtils.getCurrentUser();
        ProfileSettingsResponse response = profileSettingsService.updateSettings(currentUser.getUserId(), request);
        return ApiResponse.<ProfileSettingsResponse>builder()
                .data(response)
                .message("SUCCESS", "Profile settings updated", "ProfileService")
                .build();
    }
}

