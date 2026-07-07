package com.minhnhat.demo.features.notification.scheduler;

import com.minhnhat.demo.common.db.entity.UserEntity;
import com.minhnhat.demo.common.db.repository.UserRepository;
import com.minhnhat.demo.features.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvestmentNotificationScheduler {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 8 * * *")
    public void createDueNotifications() {
        LocalDate today = LocalDate.now();
        List<UserEntity> users = userRepository.findAll();

        for (UserEntity user : users) {
            if (user == null || user.getUserId() == null) {
                continue;
            }

            Integer dayOfMonth = user.getAutoInvestDayOfMonth();
            if (dayOfMonth == null) {
                continue;
            }

            int resolvedDay = Math.min(dayOfMonth, today.lengthOfMonth());
            if (today.getDayOfMonth() != resolvedDay) {
                continue;
            }

            boolean autoEnabled = Boolean.TRUE.equals(user.getAutoInvestEnabled());
            notificationService.createDueNotification(user.getUserId(), autoEnabled, today.atStartOfDay());
            log.info("Created due notification for userId={}, autoEnabled={}", user.getUserId(), autoEnabled);
        }
    }
}



