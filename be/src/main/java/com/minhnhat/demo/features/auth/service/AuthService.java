package com.minhnhat.demo.features.auth.service;

import com.minhnhat.demo.common.exception.LogicException;
import com.minhnhat.demo.common.mail.MailPayload;
import com.minhnhat.demo.common.mail.MailService;
import com.minhnhat.demo.common.mail.MailTemplateType;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.features.auth.dto.enums.VerificationStatus;
import com.minhnhat.demo.features.auth.dto.request.LoginRequest;
import com.minhnhat.demo.features.auth.dto.request.RefreshTokenRequest;
import com.minhnhat.demo.features.auth.dto.request.RegisterAccountRequest;
import com.minhnhat.demo.features.auth.dto.response.LoginResponse;
import com.minhnhat.demo.features.auth.dto.response.LogoutResponse;
import com.minhnhat.demo.features.auth.dto.response.RefreshTokenResponse;
import com.minhnhat.demo.features.auth.dto.response.RegisterAccountResponse;
import com.minhnhat.demo.common.db.entity.UserEntity;
import com.minhnhat.demo.common.db.repository.UserRepository;
import com.minhnhat.demo.common.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

import static com.minhnhat.demo.features.auth.constant.AuthConstant.*;
import static com.minhnhat.demo.features.auth.dto.enums.VerificationStatus.VERIFIED;

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
        RegisterContext ctx = loadData(request);
        processBusiness(ctx);
        return createResponse(ctx);
    }

    public LoginResponse login(LoginRequest request) {
        validateRequest(request);
        LoginContext ctx = loadData(request);
        processBusiness(ctx);
        return createResponse(ctx);
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
        // Validate request is not null
        if (request == null) {
            log.error("Register validation failed: request is null");
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_001), REGISTER_LOGIC_ERROR_001);
        }

        // Validate email is not empty
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            log.error("Register validation failed: email is empty");
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_001), REGISTER_LOGIC_ERROR_001);
        }

        // Validate password is not empty
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            log.error("Register validation failed: password is empty");
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_001), REGISTER_LOGIC_ERROR_001);
        }

        // Validate confirm password is not empty
        if (request.getConfirmPassword() == null || request.getConfirmPassword().trim().isEmpty()) {
            log.error("Register validation failed: confirmPassword is empty");
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_001), REGISTER_LOGIC_ERROR_001);
        }
    }

    private void validateBusiness(RegisterContext ctx) {
        if (ctx.duplicateEmail) {
            log.error("Register validation failed: email already exists={}", ctx.request.getEmail());
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_003), REGISTER_LOGIC_ERROR_003);
        }

        if (!ctx.request.getPassword().equals(ctx.request.getConfirmPassword())) {
            log.error("Register validation failed: password mismatch for email={}", ctx.request.getEmail());
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_004), REGISTER_LOGIC_ERROR_004);
        }
    }

    public LogoutResponse logout() {
        try {
            // Clear the SecurityContext
            SecurityContextHolder.clearContext();
            log.info("User logged out successfully");
            return LogoutResponse.builder()
                    .message("Logged out successfully")
                    .build();
        } catch (Exception e) {
            log.error("Logout failed", e);
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_002), REGISTER_LOGIC_ERROR_002);
        }
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        validateRequest(request);
        RefreshTokenContext ctx = loadData(request);
        processBusiness(ctx);
        return createResponse(ctx);
    }

    private void validateRequest(LoginRequest request) {
        if (request == null || request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            log.error("Login validation failed: email is empty");
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_002), REGISTER_LOGIC_ERROR_002);
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            log.error("Login validation failed: password is empty");
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_002), REGISTER_LOGIC_ERROR_002);
        }
    }

    private LoginContext loadData(LoginRequest request) {
        LoginContext ctx = new LoginContext();
        ctx.request = request;
        ctx.userEntity = userRepository.findFirstByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.error("Login validation failed: user not found for email={}", request.getEmail());
                    return new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_002), REGISTER_LOGIC_ERROR_002);
                });
        return ctx;
    }

    private void processBusiness(LoginContext ctx) {
        validateBusiness(ctx);
        prepare(ctx);
        execute(ctx);
        persist(ctx);
        afterProcess(ctx);
    }

    private void validateBusiness(LoginContext ctx) {
        validateLoginRequest(ctx.request, ctx.userEntity);
    }

    private void prepare(LoginContext ctx) {
        ctx.principal = UserPrincipal.from(ctx.userEntity);
        ctx.authentication = new UsernamePasswordAuthenticationToken(
                ctx.principal,
                null,
                ctx.principal.getAuthorities()
        );
    }

    private void execute(LoginContext ctx) {
        SecurityContextHolder.getContext().setAuthentication(ctx.authentication);
        ctx.accessToken = jwtTokenProvider.generateAccessToken(ctx.principal);
        ctx.refreshToken = jwtTokenProvider.generateRefreshToken(ctx.principal);
        ctx.userEntity.setToken(ctx.refreshToken);
    }

    private void persist(LoginContext ctx) {
        try {
            userRepository.save(ctx.userEntity);
            log.info("Refresh token saved successfully for email={}", ctx.userEntity.getEmail());
        } catch (Exception e) {
            log.error("Failed to save refresh token for email={}", ctx.userEntity.getEmail(), e);
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_001), REGISTER_LOGIC_ERROR_001);
        }
    }

    private void afterProcess(LoginContext ctx) {
        // No additional side effects.
    }

    private LoginResponse createResponse(LoginContext ctx) {
        return LoginResponse.builder()
                .userId(ctx.userEntity.getUserId())
                .email(ctx.userEntity.getEmail())
                .status(ctx.userEntity.getStatus())
                .accessToken(ctx.accessToken)
                .refreshToken(ctx.refreshToken)
                .build();
    }

    private RegisterContext loadData(RegisterAccountRequest request) {
        RegisterContext ctx = new RegisterContext();
        ctx.request = request;
        ctx.token = UUID.randomUUID().toString();
        ctx.userId = UUID.randomUUID().toString();
        ctx.duplicateEmail = userRepository.findFirstByEmail(request.getEmail()).isPresent();
        return ctx;
    }

    private void processBusiness(RegisterContext ctx) {
        validateBusiness(ctx);
        prepare(ctx);
        execute(ctx);
        persist(ctx);
        afterProcess(ctx);
    }

    private void prepare(RegisterContext ctx) {
        ctx.verifyLink = URL + ctx.token;
    }

    private void execute(RegisterContext ctx) {
        registerNewAccount(ctx.request, ctx.token, ctx.userId);
        sendVerifyMail(ctx.request, ctx.verifyLink);
    }

    private void persist(RegisterContext ctx) {
        // Persistence is already executed inside registerNewAccount.
    }

    private void afterProcess(RegisterContext ctx) {
        // No additional side effects.
    }

    private RegisterAccountResponse createResponse(RegisterContext ctx) {
        return RegisterAccountResponse.builder()
                .userId(ctx.userId)
                .email(ctx.request.getEmail())
                .build();
    }

    private void validateRequest(RefreshTokenRequest request) {
        if (request == null || request.getRefreshToken() == null || request.getRefreshToken().trim().isEmpty()) {
            log.error("Refresh token validation failed: token is empty");
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_002), REGISTER_LOGIC_ERROR_002);
        }
    }

    private RefreshTokenContext loadData(RefreshTokenRequest request) {
        RefreshTokenContext ctx = new RefreshTokenContext();
        ctx.request = request;
        return ctx;
    }

    private void processBusiness(RefreshTokenContext ctx) {
        validateBusiness(ctx);
        prepare(ctx);
        execute(ctx);
        persist(ctx);
        afterProcess(ctx);
    }

    private void validateBusiness(RefreshTokenContext ctx) {
        if (!jwtTokenProvider.validateToken(ctx.request.getRefreshToken())) {
            log.error("Refresh token validation failed: invalid or expired token");
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_002), REGISTER_LOGIC_ERROR_002);
        }

        if (!jwtTokenProvider.isRefreshToken(ctx.request.getRefreshToken())) {
            log.error("Refresh token validation failed: token is not a refresh token");
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_002), REGISTER_LOGIC_ERROR_002);
        }
    }

    private void prepare(RefreshTokenContext ctx) {
        ctx.userId = jwtTokenProvider.getUserIdFromToken(ctx.request.getRefreshToken());
        ctx.email = jwtTokenProvider.getEmailFromToken(ctx.request.getRefreshToken());
        ctx.userEntity = userRepository.findById(ctx.userId)
                .orElseThrow(() -> {
                    log.error("Refresh token validation failed: user not found for userId={}", ctx.userId);
                    return new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_002), REGISTER_LOGIC_ERROR_002);
                });

        if (ctx.userEntity.getToken() == null || !ctx.userEntity.getToken().equals(ctx.request.getRefreshToken())) {
            log.error("Refresh token validation failed: token mismatch for userId={}", ctx.userId);
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_002), REGISTER_LOGIC_ERROR_002);
        }
    }

    private void execute(RefreshTokenContext ctx) {
        ctx.newAccessToken = jwtTokenProvider.generateAccessToken(ctx.userId, ctx.email);
        ctx.newRefreshToken = jwtTokenProvider.generateRefreshToken(ctx.userId, ctx.email);
        ctx.userEntity.setToken(ctx.newRefreshToken);
    }

    private void persist(RefreshTokenContext ctx) {
        try {
            userRepository.save(ctx.userEntity);
            log.info("Refresh token renewed successfully for userId={}", ctx.userId);
        } catch (Exception e) {
            log.error("Failed to update refresh token for userId={}", ctx.userId, e);
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_001), REGISTER_LOGIC_ERROR_001);
        }
    }

    private void afterProcess(RefreshTokenContext ctx) {
        // No additional side effects.
    }

    private RefreshTokenResponse createResponse(RefreshTokenContext ctx) {
        return RefreshTokenResponse.builder()
                .accessToken(ctx.newAccessToken)
                .refreshToken(ctx.newRefreshToken)
                .build();
    }

    private static class RegisterContext {
        RegisterAccountRequest request;
        String token;
        String userId;
        String verifyLink;
        boolean duplicateEmail;
    }

    private static class LoginContext {
        LoginRequest request;
        UserEntity userEntity;
        UserPrincipal principal;
        Authentication authentication;
        String accessToken;
        String refreshToken;
    }

    private static class RefreshTokenContext {
        RefreshTokenRequest request;
        String userId;
        String email;
        UserEntity userEntity;
        String newAccessToken;
        String newRefreshToken;
    }
}
