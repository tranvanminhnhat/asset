package com.minhnhat.demo.common.db.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity extends BaseEntity {

    @Id
    @Column(name = "user_id", length = 36, nullable = false, updatable = false)
    private String userId;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "full_name", length = 255)
    private String fullName;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "is_verified", nullable = true)
    private Boolean isVerified;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "token", length = 2000)
    private String token;

    @Builder.Default
    @Column(name = "auto_invest_enabled", nullable = false)
    private Boolean autoInvestEnabled = Boolean.FALSE;

    @Column(name = "investment_style", length = 30)
    private String investmentStyle;

    @Column(name = "auto_invest_day_of_month")
    private Integer autoInvestDayOfMonth;
}

