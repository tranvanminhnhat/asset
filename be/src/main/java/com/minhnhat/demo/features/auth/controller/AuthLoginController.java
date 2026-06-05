package com.minhnhat.demo.features.auth.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.features.auth.dto.request.LoginRequest;
import com.minhnhat.demo.features.auth.dto.response.LoginResponse;
import com.minhnhat.demo.features.auth.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.minhnhat.demo.features.auth.constant.AuthConstant.LOGIN_INFO_001;

@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthLoginController {

    private final LoginService loginService;
    private final MessageService messageService;

    private static final String SERVICE_NAME = "AuthService";

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.<LoginResponse>builder()
                .data(loginService.execute(request))
                .message(LOGIN_INFO_001, messageService.get(LOGIN_INFO_001), SERVICE_NAME)
                .build();
    }
}

