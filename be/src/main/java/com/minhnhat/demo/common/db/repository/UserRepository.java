package com.minhnhat.demo.common.db.repository;

import com.minhnhat.demo.common.db.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findFirstByEmail(String email);
    Optional<UserEntity> findFirstByToken(String token);
}

