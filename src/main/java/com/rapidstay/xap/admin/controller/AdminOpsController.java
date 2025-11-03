package com.rapidstay.xap.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
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
    private final JobLauncher jobLauncher;
    private final Optional<Job> cityDataCollectorJob; // 배치가 없을 수도 있어서 Optional 처리

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

    /** 🚀 CityDataCollector 배치 즉시 실행 */
    @PostMapping("/batch/city-collector")
    public Map<String, Object> runCityCollector() {
        if (cityDataCollectorJob.isEmpty()) {
            return Map.of("status", "SKIPPED", "reason", "CityDataCollector Job not registered");
        }
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("ts", System.currentTimeMillis())
                    .toJobParameters();
            JobExecution exec = jobLauncher.run(cityDataCollectorJob.get(), params);
            System.out.println("🚀 [AdminOps] 배치 실행: " + exec.getJobId() + " / " + exec.getStatus());
            return Map.of(
                    "jobId", exec.getJobId(),
                    "status", exec.getStatus().toString()
            );
        } catch (Exception e) {
            System.err.println("❌ [AdminOps] 배치 실행 실패: " + e.getMessage());
            return Map.of("status", "FAILED", "error", e.getMessage());
        }
    }
}
