package com.minhnhat.demo.features.auth.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.exception.LogicException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.minhnhat.demo.features.auth.dto.request.LoginRequest;
import com.minhnhat.demo.features.auth.dto.request.RegisterAccountRequest;
import com.minhnhat.demo.features.auth.dto.response.LoginResponse;
import com.minhnhat.demo.features.auth.dto.response.RegisterAccountResponse;
import com.minhnhat.demo.features.auth.dto.response.LogoutResponse;
import com.minhnhat.demo.features.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.minhnhat.demo.features.auth.constant.AuthConstant.LOGIN_INFO_001;
import static com.minhnhat.demo.features.auth.constant.AuthConstant.REGISTER_INFO_001;
import static com.minhnhat.demo.features.auth.constant.AuthConstant.LOGOUT_INFO_001;

@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    private final MessageService messageService;

    private static final String SERVICE_NAME = "AuthService";

    @Value("${app.frontend.login-url:http://localhost:3000/login}")
    private String frontendLoginUrl;

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

    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam("token") String token) {
        try {
            service.verifyEmail(token);
            String redirect = frontendLoginUrl + "?verified=true";
            return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, redirect).build();
        } catch (LogicException ex) {
            // Redirect to frontend login page with a failure flag
            String redirect = frontendLoginUrl + "?verified=false";
            return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, redirect).build();
        }
    }

    @PostMapping("/logout")
    public ApiResponse<LogoutResponse> logout() {
        return ApiResponse.<LogoutResponse>builder()
                .data(service.logout())
                .message(LOGOUT_INFO_001, messageService.get(LOGOUT_INFO_001), SERVICE_NAME)
                .build();
    }
}
