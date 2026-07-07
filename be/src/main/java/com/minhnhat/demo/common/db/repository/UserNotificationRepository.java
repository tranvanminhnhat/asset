package com.minhnhat.demo.common.db.repository;

import com.minhnhat.demo.common.db.entity.UserNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotificationEntity, Long> {
    List<UserNotificationEntity> findByUserIdOrderByCreatedAtDesc(String userId);
    List<UserNotificationEntity> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, String status);
    List<UserNotificationEntity> findByStatusAndDueDateLessThanEqual(String status, LocalDateTime dueDate);
    boolean existsByUserIdAndTypeAndDueDate(String userId, String type, LocalDateTime dueDate);
    java.util.Optional<UserNotificationEntity> findFirstByUserIdAndTypeAndDueDate(String userId, String type, LocalDateTime dueDate);
}


