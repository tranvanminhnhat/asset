package com.minhnhat.demo.common.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_notifications", indexes = {
        @Index(name = "idx_notification_user", columnList = "user_id"),
        @Index(name = "idx_notification_status", columnList = "status"),
        @Index(name = "idx_notification_due_date", columnList = "due_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @Column(name = "mode", nullable = false, length = 20)
    private String mode;

    @Column(name = "target_flow", nullable = false, length = 50)
    private String targetFlow;

    @Column(name = "payload", columnDefinition = "JSON")
    private String payload;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "actioned_at")
    private LocalDateTime actionedAt;

    @Column(name = "done_at")
    private LocalDateTime doneAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}


