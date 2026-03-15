package com.minhnhat.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "USER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity extends BaseEntity {

    @Id
    @Column(name = "USER_ID", length = 36, nullable = false, updatable = false)
    private String userId;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "PASSWORD", nullable = false, length = 255)
    private String password;

    @Column(name = "STATUS", nullable = false)
    private String status;

    @Column(name = "TOKEN", nullable = false)
    private String token;
}
