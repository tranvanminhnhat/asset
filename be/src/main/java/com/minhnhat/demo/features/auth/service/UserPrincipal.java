package com.minhnhat.demo.features.auth.service;

import com.minhnhat.demo.common.db.entity.UserEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {

    private final String userId;
    private final String email;
    private final String fullName;
    private final String avatarUrl;
    private final String password;
    private final String status;
    private final Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal from(UserEntity user) {
        return new UserPrincipal(
                user.getUserId(),
                user.getEmail(),
                user.getFullName(),
                user.getAvatarUrl(),
                user.getPassword(),
                user.getStatus(),
                List.of()
        );
    }

    @Override
    public @NonNull String getUsername() {
        return email;
    }
}
