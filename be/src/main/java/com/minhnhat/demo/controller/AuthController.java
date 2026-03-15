package com.minhnhat.demo.controller;

import com.minhnhat.demo.common.response.ApiResponse;
import com.minhnhat.demo.config.MessageService;
import com.minhnhat.demo.dto.request.LoginRequest;
import com.minhnhat.demo.dto.request.RegisterAccountRequest;
import com.minhnhat.demo.dto.response.LoginResponse;
import com.minhnhat.demo.dto.response.RegisterAccountResponse;
import com.minhnhat.demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.minhnhat.demo.constant.MessageConstant.LOGIN_INFO_001;
import static com.minhnhat.demo.constant.MessageConstant.REGISTER_INFO_001;

@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    private final MessageService messageService;

    private static final String SERVICE_NAME = "AuthService";

    @PostMapping("/register")
    public ApiResponse<RegisterAccountResponse> registerAccount(@RequestBody RegisterAccountRequest request) {
        return ApiResponse.<RegisterAccountResponse>builder()
                .data(service.registerAccount(request))
                .message(REGISTER_INFO_001, messageService.get(REGISTER_INFO_001), SERVICE_NAME)
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.<LoginResponse>builder()
                .data(service.login(request))
                .message(LOGIN_INFO_001, messageService.get(LOGIN_INFO_001), SERVICE_NAME)
                .build();
    }
}
