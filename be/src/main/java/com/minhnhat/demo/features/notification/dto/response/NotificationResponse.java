package com.minhnhat.demo.features.notification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class NotificationResponse {
    private Long id;
    private String type;
    private String title;
    private String message;
    private String mode;
    private String targetFlow;
    private String status;
    private String payload;
    private LocalDateTime dueDate;
    private LocalDateTime readAt;
    private LocalDateTime actionedAt;
    private LocalDateTime doneAt;
    private LocalDateTime createdAt;
}


