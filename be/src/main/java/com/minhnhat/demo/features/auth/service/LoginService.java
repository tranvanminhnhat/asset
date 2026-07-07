package com.minhnhat.demo.features.auth.service;

import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.UserEntity;
import com.minhnhat.demo.common.db.repository.UserRepository;
import com.minhnhat.demo.common.exception.LogicException;
import com.minhnhat.demo.common.security.JwtTokenProvider;
import com.minhnhat.demo.features.auth.dto.request.LoginRequest;
import com.minhnhat.demo.features.auth.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.minhnhat.demo.features.auth.constant.AuthConstant.REGISTER_LOGIC_ERROR_001;
import static com.minhnhat.demo.features.auth.constant.AuthConstant.REGISTER_LOGIC_ERROR_002;
import static com.minhnhat.demo.features.auth.dto.enums.VerificationStatus.VERIFIED;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final UserRepository userRepository;
    private final MessageService messageService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String SERVICE_NAME = "AuthService";

    public LoginResponse execute(LoginRequest request) {
        validateRequest(request);
        Context ctx = loadData(request);
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

    private Context loadData(LoginRequest request) {
        Context ctx = new Context();
        ctx.request = request;
        ctx.userEntity = userRepository.findFirstByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.error("Login validation failed: user not found for email={}", request.getEmail());
                    return new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_002), REGISTER_LOGIC_ERROR_002);
                });
        return ctx;
    }

    private void processBusiness(Context ctx) {
        validateBusiness(ctx);
        prepare(ctx);
        execute(ctx);
        persist(ctx);
        afterProcess();
    }

    private void validateBusiness(Context ctx) {
        if (!passwordEncoder.matches(ctx.request.getPassword(), ctx.userEntity.getPassword())) {
            log.error("Login validation failed: invalid password for email={}", ctx.request.getEmail());
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_002), REGISTER_LOGIC_ERROR_002);
        }
        if (!VERIFIED.getCode().equals(ctx.userEntity.getStatus())) {
            log.error("Login validation failed: account not verified for email={}", ctx.request.getEmail());
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_002), REGISTER_LOGIC_ERROR_002);
        }
    }

    private void prepare(Context ctx) {
        ctx.principal = UserPrincipal.from(ctx.userEntity);
        ctx.authentication = new UsernamePasswordAuthenticationToken(ctx.principal, null, ctx.principal.getAuthorities());
    }

    private void execute(Context ctx) {
        SecurityContextHolder.getContext().setAuthentication(ctx.authentication);
        ctx.accessToken = jwtTokenProvider.generateAccessToken(ctx.principal);
        ctx.refreshToken = jwtTokenProvider.generateRefreshToken(ctx.principal);
        ctx.userEntity.setToken(ctx.refreshToken);
    }

    private void persist(Context ctx) {
        try {
            userRepository.save(ctx.userEntity);
            log.info("Refresh token saved successfully for email={}", ctx.userEntity.getEmail());
        } catch (Exception e) {
            log.error("Failed to save refresh token for email={}", ctx.userEntity.getEmail(), e);
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_001), REGISTER_LOGIC_ERROR_001);
        }
    }

    private void afterProcess() {
        // no-op
    }

    private LoginResponse createResponse(Context ctx) {
        return LoginResponse.builder()
                .userId(ctx.userEntity.getUserId())
                .email(ctx.userEntity.getEmail())
                .fullName(ctx.userEntity.getFullName())
                .avatarUrl(ctx.userEntity.getAvatarUrl())
                .status(ctx.userEntity.getStatus())
                .autoInvestEnabled(ctx.userEntity.getAutoInvestEnabled())
                .investmentStyle(ctx.userEntity.getInvestmentStyle())
                .autoInvestDayOfMonth(ctx.userEntity.getAutoInvestDayOfMonth())
                .flowMode(Boolean.TRUE.equals(ctx.userEntity.getAutoInvestEnabled()) ? "AUTO" : "MANUAL")
                .accessToken(ctx.accessToken)
                .refreshToken(ctx.refreshToken)
                .build();
    }

    private static class Context {
        LoginRequest request;
        UserEntity userEntity;
        UserPrincipal principal;
        Authentication authentication;
        String accessToken;
        String refreshToken;
    }
}

