package com.minhnhat.demo.common.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MailTemplateType {
    VERIFY_EMAIL("verify-email", "Verify your account");

    private final String templateName;
    private final String subject;
}
