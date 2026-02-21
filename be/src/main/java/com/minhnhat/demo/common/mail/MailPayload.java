package com.minhnhat.demo.common.mail;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class MailPayload {
    private String to;
    private MailTemplateType templateType;
    private Map<String, Object> variables;
}
