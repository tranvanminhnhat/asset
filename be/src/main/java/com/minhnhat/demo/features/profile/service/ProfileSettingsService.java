package com.minhnhat.demo.features.profile.service;

import com.minhnhat.demo.common.db.entity.UserEntity;
import com.minhnhat.demo.common.db.repository.AllocationStrategyRepository;
import com.minhnhat.demo.common.db.repository.UserRepository;
import com.minhnhat.demo.features.profile.dto.request.UpdateProfileSettingsRequest;
import com.minhnhat.demo.features.profile.dto.response.ProfileSettingsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileSettingsService {

    private final UserRepository userRepository;
    private final AllocationStrategyRepository allocationStrategyRepository;

    @Transactional(readOnly = true)
    public ProfileSettingsResponse getSettings(String userId) {
        UserEntity user = loadUser(userId);
        return mapToResponse(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public ProfileSettingsResponse updateSettings(String userId, UpdateProfileSettingsRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request must not be null");
        }

        UserEntity user = loadUser(userId);
        applyText(request.getFullName(), user::setFullName, true);
        applyText(request.getAvatarUrl(), user::setAvatarUrl, false);

        if (request.getAutoInvestEnabled() != null) {
            user.setAutoInvestEnabled(request.getAutoInvestEnabled());
        }

        if (request.getInvestmentStyle() != null) {
            String style = request.getInvestmentStyle().trim().toUpperCase();
            allocationStrategyRepository.findByCode(style)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid investmentStyle: " + request.getInvestmentStyle()));
            user.setInvestmentStyle(style);
        }

        if (request.getAutoInvestDayOfMonth() != null) {
            validateAutoInvestDay(request.getAutoInvestDayOfMonth());
            user.setAutoInvestDayOfMonth(request.getAutoInvestDayOfMonth());
        }

        if (Boolean.TRUE.equals(user.getAutoInvestEnabled())) {
            if (user.getInvestmentStyle() == null || user.getInvestmentStyle().isBlank()) {
                throw new IllegalArgumentException("investmentStyle is required when autoInvestEnabled is true");
            }
            if (user.getAutoInvestDayOfMonth() == null) {
                throw new IllegalArgumentException("autoInvestDayOfMonth is required when autoInvestEnabled is true");
            }
            validateAutoInvestDay(user.getAutoInvestDayOfMonth());
        }

        userRepository.save(user);
        return mapToResponse(user);
    }

    private UserEntity loadUser(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId is required");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private void validateAutoInvestDay(Integer dayOfMonth) {
        if (dayOfMonth == null || dayOfMonth < 1 || dayOfMonth > 31) {
            throw new IllegalArgumentException("autoInvestDayOfMonth must be between 1 and 31");
        }
    }

    private ProfileSettingsResponse mapToResponse(UserEntity user) {
        boolean autoEnabled = Boolean.TRUE.equals(user.getAutoInvestEnabled());
        LocalDate nextDate = calculateNextAutoInvestDate(user.getAutoInvestDayOfMonth());
        boolean dueToday = autoEnabled && nextDate != null && LocalDate.now().equals(nextDate);

        return ProfileSettingsResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .autoInvestEnabled(autoEnabled)
                .investmentStyle(user.getInvestmentStyle())
                .autoInvestDayOfMonth(user.getAutoInvestDayOfMonth())
                .flowMode(autoEnabled ? "AUTO" : "MANUAL")
                .nextAutoInvestAt(nextDate)
                .autoInvestDueToday(dueToday)
                .build();
    }

    private LocalDate calculateNextAutoInvestDate(Integer dayOfMonth) {
        if (dayOfMonth == null) {
            return null;
        }

        LocalDate today = LocalDate.now();
        LocalDate candidate = resolveDate(today, dayOfMonth);

        if (candidate.isBefore(today)) {
            LocalDate nextMonth = today.plusMonths(1);
            candidate = resolveDate(nextMonth, dayOfMonth);
        }

        return candidate;
    }

    private LocalDate resolveDate(LocalDate baseDate, Integer dayOfMonth) {
        int resolvedDay = Math.min(dayOfMonth, YearMonth.from(baseDate).lengthOfMonth());
        return baseDate.withDayOfMonth(resolvedDay);
    }

    private void applyText(String value, java.util.function.Consumer<String> setter, boolean normalizeEmptyToNull) {
        if (value == null) {
            return;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            if (normalizeEmptyToNull) {
                setter.accept(null);
            }
            return;
        }

        if (!trimmed.isEmpty()) {
            setter.accept(trimmed);
        }
    }
}


