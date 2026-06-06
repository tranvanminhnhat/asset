package com.minhnhat.demo.features.auth.controller;

import com.minhnhat.demo.common.exception.LogicException;
import com.minhnhat.demo.features.auth.service.VerifyEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthVerifyEmailController {

    private final VerifyEmailService verifyEmailService;

    //    @Value("${app.frontend.login-url:http://localhost:3000/login}")
    @Value("${app.frontend.login-url:https://asset-ashen.vercel.app/login}")
    private String frontendLoginUrl;

    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam("token") String token) {
        try {
            verifyEmailService.execute(token);
            String redirect = frontendLoginUrl + "?verified=true";
            return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, redirect).build();
        } catch (LogicException ex) {
            String redirect = frontendLoginUrl + "?verified=false";
            return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, redirect).build();
        }
    }
}

