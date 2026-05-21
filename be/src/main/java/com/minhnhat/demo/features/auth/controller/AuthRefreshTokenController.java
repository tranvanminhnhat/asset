package com.minhnhat.demo.features.auth.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.features.auth.dto.request.RefreshTokenRequest;
import com.minhnhat.demo.features.auth.dto.response.RefreshTokenResponse;
import com.minhnhat.demo.features.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.minhnhat.demo.features.auth.constant.AuthConstant.REFRESH_TOKEN_INFO_001;

@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthRefreshTokenController {

    private final AuthService authService;
    private final MessageService messageService;

    private static final String SERVICE_NAME = "AuthService";

    @PostMapping("/refresh-token")
    public ApiResponse<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ApiResponse.<RefreshTokenResponse>builder()
                .data(authService.refreshToken(request))
                .message(REFRESH_TOKEN_INFO_001, messageService.get(REFRESH_TOKEN_INFO_001), SERVICE_NAME)
                .build();
    }
}

