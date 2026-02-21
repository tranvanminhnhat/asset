package com.minhnhat.demo.service;

import com.minhnhat.demo.common.exception.LogicException;
import com.minhnhat.demo.common.mail.MailPayload;
import com.minhnhat.demo.common.mail.MailService;
import com.minhnhat.demo.common.mail.MailTemplateType;
import com.minhnhat.demo.config.MessageService;
import com.minhnhat.demo.dto.enums.VerificationStatus;
import com.minhnhat.demo.dto.request.RegisterAccountRequest;
import com.minhnhat.demo.dto.response.RegisterAccountResponse;
import com.minhnhat.demo.entity.UserEntity;
import com.minhnhat.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

import static com.minhnhat.demo.constant.MessageConstant.REGISTER_LOGIC_ERROR_001;
import static com.minhnhat.demo.constant.MessageConstant.REGISTER_LOGIC_ERROR_002;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    private final MailService mailService;

    private final MessageService messageService;

    private final PasswordEncoder passwordEncoder;

    private static final String SERVICE_NAME = "AuthService";

    private static final String url = "http://localhost:8080/auth/verify-email?token=";

    public RegisterAccountResponse registerAccount(RegisterAccountRequest request) {
        
        validateRequest(request);

        String token = UUID.randomUUID().toString();

        String userId = UUID.randomUUID().toString();

        registerNewAccount(request, token, userId);

        String verifyLink = url + token;

        sendVerifyMail(request, verifyLink);

        return createResponse(userId, request);
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
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_002),REGISTER_LOGIC_ERROR_002);
        }

    }

    private void registerNewAccount(RegisterAccountRequest request, String token, String userId) {

        UserEntity userEntity =
                UserEntity.builder()
                        .userId(userId)
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .token(token)
                        .isVerified(VerificationStatus.UNVERIFIED.getCode())
                        .build();

        try {
            userRepository.save(userEntity);
        } catch (Exception e) {
            throw new LogicException(SERVICE_NAME, messageService.get(REGISTER_LOGIC_ERROR_001), REGISTER_LOGIC_ERROR_001);
        }

    }

    private void validateRequest(RegisterAccountRequest request) {

        // Check exist user by email
        UserEntity userEntity = userRepository.findFirstByEmail(request.getEmail());
        if (userEntity != null) {
            throw new RuntimeException();
        }

        // Check password and confirm password is not match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException();
        }
    }
}
