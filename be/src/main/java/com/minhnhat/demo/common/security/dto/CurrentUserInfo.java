package com.minhnhat.demo.common.security.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurrentUserInfo {
    private String userId;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String role;
    private String status;
}
