package com.minhnhat.demo.features.auth.service;

import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.UserEntity;
import com.minhnhat.demo.common.db.repository.UserRepository;
import com.minhnhat.demo.common.exception.LogicException;
import com.minhnhat.demo.features.auth.dto.enums.VerificationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.minhnhat.demo.features.auth.constant.AuthConstant.REGISTER_LOGIC_ERROR_001;
import static com.minhnhat.demo.features.auth.constant.AuthConstant.REGISTER_LOGIC_ERROR_005;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerifyEmailService {

    private final UserRepository userRepository;
    private final MessageService messageService;

    private static final String SERVICE_NAME = "AuthService";

    public void execute(String token) {
        UserEntity userEntity = userRepository.findFirstByToken(token).orElse(null);
        if (userEntity == null) {
            log.error("Verify email failed: invalid token={}", token);
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_005), REGISTER_LOGIC_ERROR_005);
        }

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
}

