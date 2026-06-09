package com.minhnhat.demo.common.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_cash_balance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCashBalanceEntity {

    @Id
    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    @Column(name = "cash_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal cashAmount;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

