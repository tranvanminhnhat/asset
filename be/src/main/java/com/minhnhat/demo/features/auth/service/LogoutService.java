package com.minhnhat.demo.features.auth.service;

import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.exception.LogicException;
import com.minhnhat.demo.features.auth.dto.response.LogoutResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static com.minhnhat.demo.features.auth.constant.AuthConstant.REGISTER_LOGIC_ERROR_002;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService {

    private final MessageService messageService;
    private static final String SERVICE_NAME = "AuthService";

    public LogoutResponse execute() {
        try {
            SecurityContextHolder.clearContext();
            log.info("User logged out successfully");
            return LogoutResponse.builder().message("Logged out successfully").build();
        } catch (Exception e) {
            log.error("Logout failed", e);
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_002), REGISTER_LOGIC_ERROR_002);
        }
    }
}

