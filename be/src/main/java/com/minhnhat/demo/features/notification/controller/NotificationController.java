package com.minhnhat.demo.features.notification.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.security.SecurityUtils;
import com.minhnhat.demo.common.security.dto.CurrentUserInfo;
import com.minhnhat.demo.features.notification.dto.response.NotificationResponse;
import com.minhnhat.demo.features.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ApiResponse<List<NotificationResponse>> getNotifications() {
        CurrentUserInfo currentUser = securityUtils.getCurrentUser();
        List<NotificationResponse> data = notificationService.getNotifications(currentUser.getUserId());
        return ApiResponse.<List<NotificationResponse>>builder()
                .data(data)
                .message("SUCCESS", "Notifications retrieved", "NotificationService")
                .build();
    }

    @GetMapping("/unread")
    public ApiResponse<List<NotificationResponse>> getUnreadNotifications() {
        CurrentUserInfo currentUser = securityUtils.getCurrentUser();
        List<NotificationResponse> data = notificationService.getUnreadNotifications(currentUser.getUserId());
        return ApiResponse.<List<NotificationResponse>>builder()
                .data(data)
                .message("SUCCESS", "Unread notifications retrieved", "NotificationService")
                .build();
    }

    @PostMapping("/{id}/read")
    public ApiResponse<NotificationResponse> markRead(@PathVariable Long id) {
        return ApiResponse.<NotificationResponse>builder()
                .data(notificationService.markRead(id))
                .message("SUCCESS", "Notification marked as read", "NotificationService")
                .build();
    }

    @PostMapping("/{id}/action")
    public ApiResponse<NotificationResponse> markActioned(@PathVariable Long id) {
        return ApiResponse.<NotificationResponse>builder()
                .data(notificationService.markActioned(id))
                .message("SUCCESS", "Notification action recorded", "NotificationService")
                .build();
    }

    @PostMapping("/{id}/done")
    public ApiResponse<NotificationResponse> markDone(@PathVariable Long id) {
        return ApiResponse.<NotificationResponse>builder()
                .data(notificationService.markDone(id))
                .message("SUCCESS", "Notification completed", "NotificationService")
                .build();
    }
}

