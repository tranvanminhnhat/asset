package com.minhnhat.demo.features.dashboard.service;

import com.minhnhat.demo.features.dashboard.dto.response.DashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetDashboardByUserIdUseCaseService {

    private final IDashboardService dashboardService;

    public DashboardResponse execute(String userId) {
        return dashboardService.getDashboardByUserId(userId);
    }
}

