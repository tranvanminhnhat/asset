package com.minhnhat.demo.features.auth.repository;

import com.minhnhat.demo.features.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findFirstByEmail(String email);
    Optional<UserEntity> findFirstByToken(String token);
}
