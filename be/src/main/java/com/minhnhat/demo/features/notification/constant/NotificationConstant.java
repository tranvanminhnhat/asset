package com.minhnhat.demo.features.notification.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("unused")
public class NotificationConstant {
    public static final String TYPE_AUTO_INVEST_DUE = "AUTO_INVEST_DUE";
    public static final String TYPE_MANUAL_INVEST_DUE = "MANUAL_INVEST_DUE";

    public static final String MODE_AUTO = "AUTO";
    public static final String MODE_MANUAL = "MANUAL";

    public static final String FLOW_AUTO_INVEST = "AUTO_INVEST";
    public static final String FLOW_MANUAL_INVESTMENT = "MANUAL_INVESTMENT";

    public static final String STATUS_NEW = "NEW";
    public static final String STATUS_READ = "READ";
    public static final String STATUS_ACTIONED = "ACTIONED";
    public static final String STATUS_DONE = "DONE";
    public static final String STATUS_EXPIRED = "EXPIRED";
}


