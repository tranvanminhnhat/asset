package com.minhnhat.demo.controller;

import com.minhnhat.demo.common.response.ApiResponse;
import com.minhnhat.demo.config.MessageService;
import com.minhnhat.demo.common.exception.LogicException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.minhnhat.demo.dto.request.LoginRequest;
import com.minhnhat.demo.dto.request.RegisterAccountRequest;
import com.minhnhat.demo.dto.response.LoginResponse;
import com.minhnhat.demo.dto.response.RegisterAccountResponse;
import com.minhnhat.demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.minhnhat.demo.constant.MessageConstant.LOGIN_INFO_001;
import static com.minhnhat.demo.constant.MessageConstant.REGISTER_INFO_001;
import static com.minhnhat.demo.constant.MessageConstant.REGISTER_INFO_002;

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
            // Redirect to frontend login page with failure flag
            String redirect = frontendLoginUrl + "?verified=false";
            return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, redirect).build();
        }
    }
}
