package com.rapidstay.xap.admin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_user")
@Getter
@Setter
public class AdminUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String role;   // ✅ 이 한 줄 추가

    @Column(name = "is_active")
    private boolean isActive;

    private LocalDateTime createdAt;
}

