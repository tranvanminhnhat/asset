package com.minhnhat.demo.repository;

import com.minhnhat.demo.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findFirstByEmail(String email);
}
