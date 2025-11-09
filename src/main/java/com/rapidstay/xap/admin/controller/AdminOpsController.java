package com.rapidstay.xap.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import com.rapidstay.xap.admin.service.AdminCityService;

import java.util.*;

@RestController
@RequestMapping("/admin/ops")
@RequiredArgsConstructor
public class AdminOpsController {

    private final StringRedisTemplate stringRedisTemplate;
    private final AdminCityService adminCityService;

    /** 🧹 city:* 캐시 전체 삭제 */
    @DeleteMapping("/cache/flush")
    public Map<String, Object> flushCityCache() {
        Set<String> keys = stringRedisTemplate.keys("city:*");
        long deleted = 0;
        if (keys != null && !keys.isEmpty()) {
            deleted = stringRedisTemplate.delete(keys);
        }
        System.out.println("🧹 [AdminOps] Redis 캐시 삭제: " + deleted + "건");
        return Map.of("deleted", deleted, "status", "OK");
    }

    /** 🔁 Redis city:list 재빌드 */
    @PostMapping("/cache/rebuild")
    public Map<String, Object> rebuildCache() {
        adminCityService.rebuildCityListCache();
        System.out.println("🔁 [AdminOps] city:list 캐시 재빌드 완료");
        return Map.of("status", "OK");
    }

    /** 🚀 배치 트리거 제거됨 — Admin에서는 Batch 기능 비활성화 상태 */
    @PostMapping("/batch/city-collector")
    public Map<String, Object> runCityCollector() {
        System.out.println("⚙️ [AdminOps] 배치 기능은 현재 비활성화 상태입니다.");
        return Map.of(
                "status", "SKIPPED",
                "reason", "BatchAutoConfiguration excluded in Admin module"
        );
    }
}
