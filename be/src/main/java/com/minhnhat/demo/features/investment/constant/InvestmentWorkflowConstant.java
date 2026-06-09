package com.minhnhat.demo.features.investment.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InvestmentWorkflowConstant {
    // Session Status
    public static final String STATUS_DRAFT = "DRAFT";
    public static final String STATUS_PROCESSING = "PROCESSING";
    public static final String STATUS_REVIEW = "REVIEW";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    // Current Step
    public static final String STEP_RECOMMEND = "RECOMMEND";
    public static final String STEP_PROCESS_ASSET = "PROCESS_ASSET";
    public static final String STEP_REVIEW = "REVIEW";
    public static final String STEP_FINALIZE = "FINALIZE";

    // Allocation Status
    public static final String ALLOCATION_NOT_STARTED = "NOT_STARTED";
    public static final String ALLOCATION_IN_PROGRESS = "IN_PROGRESS";
    public static final String ALLOCATION_COMPLETED = "COMPLETED";
    public static final String ALLOCATION_LOCKED = "LOCKED";

    // Asset Types
    public static final String ASSET_TYPE_EMERGENCY = "EMERGENCY";
    public static final String ASSET_TYPE_GOLD = "GOLD";
    public static final String ASSET_TYPE_ETF = "ETF";
    public static final String ASSET_TYPE_STOCK = "STOCK";

    // Event Types
    public static final String EVENT_SESSION_CREATED = "SESSION_CREATED";
    public static final String EVENT_RECOMMENDATION_GENERATED = "RECOMMENDATION_GENERATED";
    public static final String EVENT_ASSET_SELECTED = "ASSET_SELECTED";
    public static final String EVENT_ALLOCATION_COMPLETED = "ALLOCATION_COMPLETED";
    public static final String EVENT_SESSION_REVIEWED = "SESSION_REVIEWED";
    public static final String EVENT_SESSION_FINALIZED = "SESSION_FINALIZED";

    // Message Keys
    public static final String CREATE_SESSION_INFO = "investment.session.create.info.001";
    public static final String GET_RECOMMENDATION_INFO = "investment.session.getRecommendation.info.001";
    public static final String GET_DETAIL_INFO = "investment.session.getDetail.info.001";
    public static final String GET_ASSETS_INFO = "investment.asset.getAll.info.001";
    public static final String SELECT_ASSET_INFO = "investment.allocation.selectAsset.info.001";
    public static final String COMPLETE_ALLOCATION_INFO = "investment.allocation.complete.info.001";
    public static final String REVIEW_SESSION_INFO = "investment.session.review.info.001";
    public static final String FINALIZE_SESSION_INFO = "investment.session.finalize.info.001";

    public static final String ERROR_SESSION_NOT_FOUND = "investment.error.sessionNotFound";
    public static final String ERROR_ALLOCATION_NOT_FOUND = "investment.error.allocationNotFound";
    public static final String ERROR_ASSET_NOT_FOUND = "investment.error.assetNotFound";
    public static final String ERROR_ALLOCATION_LOCKED = "investment.error.allocationLocked";
    public static final String ERROR_INVALID_QUANTITY = "investment.error.invalidQuantity";
    public static final String ERROR_ALLOCATION_NOT_COMPLETED = "investment.error.allocationNotCompleted";

    public static final String SERVICE_NAME = "InvestmentWorkflowService";
}

