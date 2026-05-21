package com.minhnhat.demo.features.auth.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.features.auth.dto.response.LogoutResponse;
import com.minhnhat.demo.features.auth.service.LogoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.minhnhat.demo.features.auth.constant.AuthConstant.LOGOUT_INFO_001;

@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthLogoutController {

    private final LogoutService logoutService;
    private final MessageService messageService;

    private static final String SERVICE_NAME = "AuthService";

    @PostMapping("/logout")
    public ApiResponse<LogoutResponse> logout() {
        return ApiResponse.<LogoutResponse>builder()
                .data(logoutService.execute())
                .message(LOGOUT_INFO_001, messageService.get(LOGOUT_INFO_001), SERVICE_NAME)
                .build();
    }
}

