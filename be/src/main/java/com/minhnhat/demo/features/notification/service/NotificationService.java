package com.minhnhat.demo.features.notification.service;

import com.minhnhat.demo.common.db.entity.UserNotificationEntity;
import com.minhnhat.demo.common.db.repository.UserNotificationRepository;
import com.minhnhat.demo.features.notification.constant.NotificationConstant;
import com.minhnhat.demo.features.notification.dto.response.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserNotificationRepository userNotificationRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(String userId) {
        return userNotificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(String userId) {
        return userNotificationRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, NotificationConstant.STATUS_NEW).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public NotificationResponse createDueNotification(String userId, boolean autoMode, LocalDateTime dueDate) {
        String type = autoMode ? NotificationConstant.TYPE_AUTO_INVEST_DUE : NotificationConstant.TYPE_MANUAL_INVEST_DUE;

        return userNotificationRepository.findFirstByUserIdAndTypeAndDueDate(userId, type, dueDate)
                .map(this::toResponse)
                .orElseGet(() -> toResponse(userNotificationRepository.save(UserNotificationEntity.builder()
                        .userId(userId)
                        .type(type)
                        .title(autoMode ? "Hôm nay là ngày đầu tư tự động" : "Đã đến kỳ đầu tư")
                        .message(autoMode
                                ? "Bạn có một phiên đầu tư tự động đang chờ xác nhận số tiền."
                                : "Bạn có một phiên đầu tư cần thực hiện theo flow hiện tại.")
                        .mode(autoMode ? NotificationConstant.MODE_AUTO : NotificationConstant.MODE_MANUAL)
                        .targetFlow(autoMode ? NotificationConstant.FLOW_AUTO_INVEST : NotificationConstant.FLOW_MANUAL_INVESTMENT)
                        .payload("{}")
                        .status(NotificationConstant.STATUS_NEW)
                        .dueDate(dueDate)
                        .build())));
    }

    @Transactional(readOnly = true)
    public boolean existsDueNotification(String userId, boolean autoMode, LocalDateTime dueDate) {
        String type = autoMode ? NotificationConstant.TYPE_AUTO_INVEST_DUE : NotificationConstant.TYPE_MANUAL_INVEST_DUE;
        return userNotificationRepository.findFirstByUserIdAndTypeAndDueDate(userId, type, dueDate).isPresent();
    }

    @Transactional(rollbackFor = Exception.class)
    public NotificationResponse markRead(Long id) {
        UserNotificationEntity notification = load(id);
        if (notification.getReadAt() == null) {
            notification.setReadAt(LocalDateTime.now());
        }
        notification.setStatus(NotificationConstant.STATUS_READ);
        return toResponse(userNotificationRepository.save(notification));
    }

    @Transactional(rollbackFor = Exception.class)
    public NotificationResponse markActioned(Long id) {
        UserNotificationEntity notification = load(id);
        notification.setActionedAt(LocalDateTime.now());
        notification.setStatus(NotificationConstant.STATUS_ACTIONED);
        return toResponse(userNotificationRepository.save(notification));
    }

    @Transactional(rollbackFor = Exception.class)
    public NotificationResponse markDone(Long id) {
        UserNotificationEntity notification = load(id);
        notification.setDoneAt(LocalDateTime.now());
        notification.setStatus(NotificationConstant.STATUS_DONE);
        return toResponse(userNotificationRepository.save(notification));
    }

    private UserNotificationEntity load(Long id) {
        return userNotificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
    }

    private NotificationResponse toResponse(UserNotificationEntity entity) {
        return NotificationResponse.builder()
                .id(entity.getId())
                .type(entity.getType())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .mode(entity.getMode())
                .targetFlow(entity.getTargetFlow())
                .status(entity.getStatus())
                .payload(entity.getPayload())
                .dueDate(entity.getDueDate())
                .readAt(entity.getReadAt())
                .actionedAt(entity.getActionedAt())
                .doneAt(entity.getDoneAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}




