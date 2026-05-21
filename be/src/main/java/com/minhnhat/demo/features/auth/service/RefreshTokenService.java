package com.minhnhat.demo.features.auth.service;

import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.UserEntity;
import com.minhnhat.demo.common.db.repository.UserRepository;
import com.minhnhat.demo.common.exception.LogicException;
import com.minhnhat.demo.common.security.JwtTokenProvider;
import com.minhnhat.demo.features.auth.dto.request.RefreshTokenRequest;
import com.minhnhat.demo.features.auth.dto.response.RefreshTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.minhnhat.demo.features.auth.constant.AuthConstant.REGISTER_LOGIC_ERROR_001;
import static com.minhnhat.demo.features.auth.constant.AuthConstant.REGISTER_LOGIC_ERROR_002;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final UserRepository userRepository;
    private final MessageService messageService;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String SERVICE_NAME = "AuthService";

    public RefreshTokenResponse execute(RefreshTokenRequest request) {
        validateRequest(request);
        Context ctx = loadData(request);
        processBusiness(ctx);
        return createResponse(ctx);
    }

    private void validateRequest(RefreshTokenRequest request) {
        if (request == null || request.getRefreshToken() == null || request.getRefreshToken().trim().isEmpty()) {
            log.error("Refresh token validation failed: token is empty");
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_002), REGISTER_LOGIC_ERROR_002);
        }
    }

    private Context loadData(RefreshTokenRequest request) {
        Context ctx = new Context();
        ctx.request = request;
        return ctx;
    }

    private void processBusiness(Context ctx) {
        validateBusiness(ctx);
        prepare(ctx);
        execute(ctx);
        persist(ctx);
        afterProcess(ctx);
    }

    private void validateBusiness(Context ctx) {
        if (!jwtTokenProvider.validateToken(ctx.request.getRefreshToken())) {
            log.error("Refresh token validation failed: invalid or expired token");
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_002), REGISTER_LOGIC_ERROR_002);
        }
        if (!jwtTokenProvider.isRefreshToken(ctx.request.getRefreshToken())) {
            log.error("Refresh token validation failed: token is not a refresh token");
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_002), REGISTER_LOGIC_ERROR_002);
        }
    }

    private void prepare(Context ctx) {
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

    private void execute(Context ctx) {
        ctx.newAccessToken = jwtTokenProvider.generateAccessToken(ctx.userId, ctx.email);
        ctx.newRefreshToken = jwtTokenProvider.generateRefreshToken(ctx.userId, ctx.email);
        ctx.userEntity.setToken(ctx.newRefreshToken);
    }

    private void persist(Context ctx) {
        try {
            userRepository.save(ctx.userEntity);
            log.info("Refresh token renewed successfully for userId={}", ctx.userId);
        } catch (Exception e) {
            log.error("Failed to update refresh token for userId={}", ctx.userId, e);
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_001), REGISTER_LOGIC_ERROR_001);
        }
    }

    private void afterProcess(Context ctx) {
        // no-op
    }

    private RefreshTokenResponse createResponse(Context ctx) {
        return RefreshTokenResponse.builder()
                .accessToken(ctx.newAccessToken)
                .refreshToken(ctx.newRefreshToken)
                .build();
    }

    private static class Context {
        RefreshTokenRequest request;
        String userId;
        String email;
        UserEntity userEntity;
        String newAccessToken;
        String newRefreshToken;
    }
}

