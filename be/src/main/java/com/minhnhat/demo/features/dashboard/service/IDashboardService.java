package com.minhnhat.demo.features.dashboard.service;

import com.minhnhat.demo.features.dashboard.dto.response.DashboardResponse;

/**
 * Dashboard Service Interface
 * Handles aggregated portfolio dashboard logic
 */
public interface IDashboardService {

    /**
     * Get dashboard data for current user
     * Aggregates investments across all plans by asset type
     *
     * @return DashboardResponse with summary and portfolio items
     */
    DashboardResponse getDashboard();

    /**
     * Get dashboard data for a specific user
     *
     * @param userId The user ID to get dashboard for
     * @return DashboardResponse with summary and portfolio items
     */
    DashboardResponse getDashboardByUserId(String userId);
}

