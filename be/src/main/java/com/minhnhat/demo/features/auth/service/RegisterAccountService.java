package com.minhnhat.demo.features.auth.service;

import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.common.db.entity.UserEntity;
import com.minhnhat.demo.common.db.repository.UserRepository;
import com.minhnhat.demo.common.exception.LogicException;
import com.minhnhat.demo.common.mail.MailPayload;
import com.minhnhat.demo.common.mail.MailService;
import com.minhnhat.demo.common.mail.MailTemplateType;
import com.minhnhat.demo.features.auth.dto.enums.VerificationStatus;
import com.minhnhat.demo.features.auth.dto.request.RegisterAccountRequest;
import com.minhnhat.demo.features.auth.dto.response.RegisterAccountResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

import static com.minhnhat.demo.features.auth.constant.AuthConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterAccountService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final MessageService messageService;
    private final PasswordEncoder passwordEncoder;

    private static final String SERVICE_NAME = "AuthService";
    private static final String URL = "http://localhost:8080/auth/verify-email?token=";

    public RegisterAccountResponse execute(RegisterAccountRequest request) {
        validateRequest(request);
        Context ctx = loadData(request);
        processBusiness(ctx);
        return createResponse(ctx);
    }

    private void validateRequest(RegisterAccountRequest request) {
        if (request == null) {
            log.error("Register validation failed: request is null");
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_001), REGISTER_LOGIC_ERROR_001);
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            log.error("Register validation failed: email is empty");
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_001), REGISTER_LOGIC_ERROR_001);
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            log.error("Register validation failed: password is empty");
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_001), REGISTER_LOGIC_ERROR_001);
        }
        if (request.getConfirmPassword() == null || request.getConfirmPassword().trim().isEmpty()) {
            log.error("Register validation failed: confirmPassword is empty");
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_001), REGISTER_LOGIC_ERROR_001);
        }
    }

    private Context loadData(RegisterAccountRequest request) {
        Context ctx = new Context();
        ctx.request = request;
        ctx.token = UUID.randomUUID().toString();
        ctx.userId = UUID.randomUUID().toString();
        ctx.verifyLink = URL + ctx.token;
        ctx.duplicateEmail = userRepository.findFirstByEmail(request.getEmail()).isPresent();
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
        if (ctx.duplicateEmail) {
            log.error("Register validation failed: email already exists={}", ctx.request.getEmail());
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_003), REGISTER_LOGIC_ERROR_003);
        }
        if (!ctx.request.getPassword().equals(ctx.request.getConfirmPassword())) {
            log.error("Register validation failed: password mismatch for email={}", ctx.request.getEmail());
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_004), REGISTER_LOGIC_ERROR_004);
        }
    }

    private void prepare(Context ctx) {
        // no-op
    }

    private void execute(Context ctx) {
        UserEntity userEntity = UserEntity.builder()
                .userId(ctx.userId)
                .email(ctx.request.getEmail())
                .password(passwordEncoder.encode(ctx.request.getPassword()))
                .token(ctx.token)
                .status(VerificationStatus.UNVERIFIED.getCode())
                .build();
        try {
            userRepository.save(userEntity);
        } catch (Exception e) {
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_001), REGISTER_LOGIC_ERROR_001);
        }

        MailPayload payload = MailPayload.builder()
                .to(ctx.request.getEmail())
                .templateType(MailTemplateType.VERIFY_EMAIL)
                .variables(Map.of("username", "demo", "verifyLink", ctx.verifyLink, "expiredAt", "15 minutes"))
                .build();
        try {
            mailService.send(payload);
        } catch (Exception e) {
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_002), REGISTER_LOGIC_ERROR_002);
        }
    }

    private void persist(Context ctx) {
        // persistence already handled in execute
    }

    private void afterProcess(Context ctx) {
        // no-op
    }

    private RegisterAccountResponse createResponse(Context ctx) {
        return RegisterAccountResponse.builder()
                .userId(ctx.userId)
                .email(ctx.request.getEmail())
                .build();
    }

    private static class Context {
        RegisterAccountRequest request;
        String token;
        String userId;
        String verifyLink;
        boolean duplicateEmail;
    }
}

