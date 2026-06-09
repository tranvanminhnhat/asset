package com.minhnhat.demo.common.db.repository;

import com.minhnhat.demo.common.db.entity.UserCashBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCashBalanceRepository extends JpaRepository<UserCashBalanceEntity, String> {
    Optional<UserCashBalanceEntity> findByUserId(String userId);
}

