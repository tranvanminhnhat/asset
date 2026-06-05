package com.minhnhat.demo.features.dashboard.controller;

import com.minhnhat.demo.common.base.response.ApiResponse;
import com.minhnhat.demo.common.config.MessageService;
import com.minhnhat.demo.features.dashboard.dto.response.DashboardResponse;
import com.minhnhat.demo.features.dashboard.service.GetDashboardByUserIdService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.minhnhat.demo.features.dashboard.constant.DashboardConstant.DASHBOARD_INFO_001;
import static com.minhnhat.demo.features.dashboard.constant.DashboardConstant.SERVICE_NAME;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardByUserIdController {

    private final GetDashboardByUserIdService getDashboardByUserIdService;
    private final MessageService messageService;

    @GetMapping("/{userId}")
    public ApiResponse<DashboardResponse> getDashboardByUserId(@PathVariable String userId) {
        DashboardResponse data = getDashboardByUserIdService.execute(userId);

        return ApiResponse.<DashboardResponse>builder()
                .data(data)
                .message(DASHBOARD_INFO_001, messageService.get(DASHBOARD_INFO_001), SERVICE_NAME)
                .build();
    }
}

