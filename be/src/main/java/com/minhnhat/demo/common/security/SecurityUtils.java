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
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null) {
                log.warn("Authentication is null in SecurityContext");
                throw new RuntimeException("User not authenticated");
            }

            if (!(authentication.getPrincipal() instanceof UserPrincipal principal)) {
                log.warn("Authentication principal is not UserPrincipal. Principal type: {}", 
                        authentication.getPrincipal().getClass().getName());
                throw new RuntimeException("User not authenticated");
            }

            log.debug("Retrieved current user: {}", principal.getUserId());
            return CurrentUserInfo.builder()
                    .userId(principal.getUserId())
                    .email(principal.getEmail())
                    .build();
        } catch (Exception e) {
            log.error("Error getting current user from SecurityContext", e);
            throw new RuntimeException("User not authenticated", e);
        }
    }
}
