package com.rapidstay.xap.admin.service;

import com.rapidstay.xap.admin.dto.LoginRequest;
import com.rapidstay.xap.admin.dto.LoginResponse;
import com.rapidstay.xap.admin.entity.AdminUser;
import com.rapidstay.xap.admin.repository.AdminUserRepository;
import com.rapidstay.xap.admin.util.JwtTokenProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;   // ✅ 주입
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest req) {
        AdminUser user = adminUserRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("🔐 로그인 시도 - username: {}", req.getUsername());
        log.info("입력 비번: {}", req.getPassword());
        log.info("DB 비번: {}", user.getPassword());

        if (!user.isActive()) {
            throw new RuntimeException("User disabled");
        }

        boolean matches = passwordEncoder.matches(req.getPassword(), user.getPassword());
        log.info("비밀번호 일치 여부: {}", matches);   // ✅ 확인용 로그

        if (!matches) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtTokenProvider.createToken(
                user.getUsername(),
                List.of(user.getRole())
        );

        return new LoginResponse(user.getUsername(), token);
    }

    @PostConstruct
    public void checkEncoder() {
        log.info("현재 사용 중인 PasswordEncoder = {}", passwordEncoder.getClass().getName());
    }
}
