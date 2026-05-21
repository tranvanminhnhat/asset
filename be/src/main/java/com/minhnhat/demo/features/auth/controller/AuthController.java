package com.minhnhat.demo.features.auth.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.features.auth.dto.request.RegisterAccountRequest;
import com.minhnhat.demo.features.auth.dto.response.RegisterAccountResponse;
import com.minhnhat.demo.features.auth.service.RegisterAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.minhnhat.demo.features.auth.constant.AuthConstant.REGISTER_INFO_001;

/**
 * Auth Controller - Handles user authentication and registration
 */
@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterAccountService registerAccountService;

    private final MessageService messageService;

    private static final String SERVICE_NAME = "AuthService";

    @PostMapping("/register")
    public ApiResponse<RegisterAccountResponse> registerAccount(@RequestBody RegisterAccountRequest request) {
        return ApiResponse.<RegisterAccountResponse>builder()
                .data(registerAccountService.execute(request))
                .message(REGISTER_INFO_001, messageService.get(REGISTER_INFO_001), SERVICE_NAME)
                .build();
    }
}
