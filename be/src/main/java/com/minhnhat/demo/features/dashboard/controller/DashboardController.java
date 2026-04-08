package com.minhnhat.demo.features.dashboard.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.features.dashboard.dto.response.DashboardResponse;
import com.minhnhat.demo.features.dashboard.service.GetDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.minhnhat.demo.features.dashboard.constant.DashboardConstant.DASHBOARD_INFO_001;
import static com.minhnhat.demo.features.dashboard.constant.DashboardConstant.SERVICE_NAME;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final GetDashboardService getDashboardService;
    private final MessageService messageService;

    @GetMapping
    public ApiResponse<DashboardResponse> getDashboard() {

        return ApiResponse.<DashboardResponse>builder()
                .data(getDashboardService.getDashboard())
                .message(DASHBOARD_INFO_001, messageService.get(DASHBOARD_INFO_001), SERVICE_NAME)
                .build();
    }
}


