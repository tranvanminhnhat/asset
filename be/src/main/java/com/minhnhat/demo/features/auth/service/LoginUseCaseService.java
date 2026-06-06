package com.minhnhat.demo.features.auth.service;

import com.minhnhat.demo.features.auth.dto.request.LoginRequest;
import com.minhnhat.demo.features.auth.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUseCaseService {

    private final AuthService authService;

    public LoginResponse execute(LoginRequest request) {
        return authService.login(request);
    }
}

