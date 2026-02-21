package com.minhnhat.demo.repository;

import com.minhnhat.demo.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    UserEntity findFirstByEmail(String email);
}
