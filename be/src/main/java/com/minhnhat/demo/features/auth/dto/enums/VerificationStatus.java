package com.minhnhat.demo.features.auth.dto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VerificationStatus {
    UNVERIFIED("01"),
    VERIFIED("02");
    private final String code;
}
