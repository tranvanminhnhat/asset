package com.minhnhat.demo.features.auth.service;

import com.minhnhat.demo.features.auth.dto.request.RegisterAccountRequest;
import com.minhnhat.demo.features.auth.dto.response.RegisterAccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterAccountUseCaseService {

    private final AuthService authService;

    public RegisterAccountResponse execute(RegisterAccountRequest request) {
        return authService.registerAccount(request);
    }
}

