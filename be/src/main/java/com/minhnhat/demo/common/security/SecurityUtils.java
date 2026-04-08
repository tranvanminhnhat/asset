package com.minhnhat.demo.common.security;

import com.minhnhat.demo.common.security.dto.CurrentUserInfo;
import com.minhnhat.demo.features.auth.service.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SecurityUtils {

    public CurrentUserInfo getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new RuntimeException("User not authenticated");
        }

        return CurrentUserInfo.builder()
                .userId(principal.getUserId())
                .build();
    }
}
