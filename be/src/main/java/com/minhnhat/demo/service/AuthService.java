package com.minhnhat.demo.service;

import com.minhnhat.demo.common.exception.LogicException;
import com.minhnhat.demo.common.mail.MailPayload;
import com.minhnhat.demo.common.mail.MailService;
import com.minhnhat.demo.common.mail.MailTemplateType;
import com.minhnhat.demo.config.MessageService;
import com.minhnhat.demo.dto.enums.VerificationStatus;
import com.minhnhat.demo.dto.request.LoginRequest;
import com.minhnhat.demo.dto.request.RegisterAccountRequest;
import com.minhnhat.demo.dto.response.LoginResponse;
import com.minhnhat.demo.dto.response.RegisterAccountResponse;
import com.minhnhat.demo.entity.UserEntity;
import com.minhnhat.demo.repository.UserRepository;
import com.minhnhat.demo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

import static com.minhnhat.demo.constant.MessageConstant.*;
import static com.minhnhat.demo.dto.enums.VerificationStatus.VERIFIED;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;

    private final MailService mailService;

    private final MessageService messageService;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private static final String SERVICE_NAME = "AuthService";

    private static final String URL = "http://localhost:8080/auth/verify-email?token=";

    public RegisterAccountResponse registerAccount(RegisterAccountRequest request) {

        validateRequest(request);

        String token = UUID.randomUUID().toString();

        String userId = UUID.randomUUID().toString();

        registerNewAccount(request, token, userId);

        String verifyLink = URL + token;

        sendVerifyMail(request, verifyLink);

        return createResponse(userId, request);
    }

    public LoginResponse login(LoginRequest request) {
        // Get user entity
        UserEntity userEntity = userRepository.findFirstByEmail(request.getEmail()).orElse(null);

        // Validate request
        validateLoginRequest(request, userEntity);

        // Create User Principal
        UserPrincipal principal = UserPrincipal.from(userEntity);

        // Set SecurityContext
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate access token
        String accessToken = jwtTokenProvider.generateToken(principal);

        // Create response
        return LoginResponse.builder()
                .userId(userEntity.getUserId())
                .email(userEntity.getEmail())
                .status(userEntity.getStatus())
                .accessToken(accessToken)
                .build();
    }

    private void validateLoginRequest(LoginRequest request, UserEntity userEntity) {

        // Check exist user by email
        if (userEntity == null) {
            log.error("Login validation failed: user not found for email={}", request.getEmail());
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_002), REGISTER_LOGIC_ERROR_002);
        }

        // Check password correctly
        if (!passwordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
            log.error("Login validation failed: invalid password for email={}", request.getEmail());
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_002), REGISTER_LOGIC_ERROR_002);
        }

        // Check account status
        if (!VERIFIED.getCode().equals(userEntity.getStatus())) {
            log.error("Login validation failed: account not verified for email={}", request.getEmail());
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_002), REGISTER_LOGIC_ERROR_002);
        }
    }


    private RegisterAccountResponse createResponse(String userId, RegisterAccountRequest request) {
        return RegisterAccountResponse.builder()
                .userId(userId)
                .email(request.getEmail())
                .build();
    }

    private void sendVerifyMail(RegisterAccountRequest request, String verifyLink) {
        MailPayload payload = MailPayload.builder()
                .to(request.getEmail())
                .templateType(MailTemplateType.VERIFY_EMAIL)
                .variables(Map.of(
                        "username", "demo",
                        "verifyLink", verifyLink,
                        "expiredAt", "15 minutes"
                ))
                .build();
        try {
            mailService.send(payload);
        } catch (Exception e) {
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_002), REGISTER_LOGIC_ERROR_002);
        }

    }

    private void registerNewAccount(RegisterAccountRequest request, String token, String userId) {

        UserEntity userEntity =
                UserEntity.builder()
                        .userId(userId)
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .token(token)
                        .status(VerificationStatus.UNVERIFIED.getCode())
                        .build();

        try {
            userRepository.save(userEntity);
        } catch (Exception e) {
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_001), REGISTER_LOGIC_ERROR_001);
        }

    }

    public void verifyEmail(String token) {
        // Find user by token
        UserEntity userEntity = userRepository.findFirstByToken(token).orElse(null);

        if (userEntity == null) {
            log.error("Verify email failed: invalid token={}", token);
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_005), REGISTER_LOGIC_ERROR_005);
        }

        // Update status to VERIFIED and clear token
        userEntity.setStatus(VerificationStatus.VERIFIED.getCode());
        userEntity.setToken("");

        try {
            userRepository.save(userEntity);
            log.info("User email verified successfully for email={}", userEntity.getEmail());
        } catch (Exception e) {
            log.error("Failed to update user status during email verification for email={}", userEntity.getEmail(), e);
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_001), REGISTER_LOGIC_ERROR_001);
        }
    }

    private void validateRequest(RegisterAccountRequest request) {
        // Check if email already exists
        if (userRepository.findFirstByEmail(request.getEmail()).isPresent()) {
            log.error("Register validation failed: email already exists={}", request.getEmail());
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_003), REGISTER_LOGIC_ERROR_003);
        }

        // Check password and confirm password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.error("Register validation failed: password mismatch for email={}", request.getEmail());
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_004), REGISTER_LOGIC_ERROR_004);
        }
    }
}
