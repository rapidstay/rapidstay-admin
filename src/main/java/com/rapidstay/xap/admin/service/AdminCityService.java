package com.rapidstay.xap.admin.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapidstay.xap.common.dto.CityDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Admin 전용 도시 관리 서비스
 * Redis 캐싱 및 API 호출 구조는 API 모듈 CityService 패턴과 동일하게 유지.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AdminCityService {

    private final RedisTemplate<String, CityDTO> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${rapidstay.api.base-url:http://localhost:8081}")
    private String apiBaseUrl;

    private static final String CITY_LIST_KEY = "city:list";
    private static final String CITY_KEY_PREFIX = "city:";

    /** 🔍 도시 검색 or 전체 목록 */
    @Transactional(readOnly = true)
    public List<CityDTO> list(String query) {
        try {
            String json = stringRedisTemplate.opsForValue().get(CITY_LIST_KEY);
            if (json != null && !json.isBlank()) {
                List<CityDTO> list = objectMapper.readValue(json, new TypeReference<>() {});
                if (query == null || query.isBlank()) return list;

                String lower = query.toLowerCase();
                return list.stream()
                        .filter(c ->
                                (c.getCityName() != null && c.getCityName().toLowerCase().contains(lower)) ||
                                        (c.getCityNameKr() != null && c.getCityNameKr().contains(query)))
                        .limit(20)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            System.err.println("❌ [AdminCityService] Redis 목록 조회 실패: " + e.getMessage());
        }

        // fallback → API 서버 호출
        String url = apiBaseUrl + "/api/cities";
        if (query != null && !query.isBlank()) {
            url += "/search?query=" + query;
        }

        try {
            CityDTO[] response = restTemplate.getForObject(url, CityDTO[].class);
            return response != null ? Arrays.asList(response) : List.of();
        } catch (Exception e) {
            System.err.println("❌ [AdminCityService] API 호출 실패: " + e.getMessage());
            return List.of();
        }
    }

    /** 🏗️ 도시 생성 */
    public CityDTO create(CityDTO dto) {
        String url = apiBaseUrl + "/api/cities";
        CityDTO created = restTemplate.postForObject(url, dto, CityDTO.class);
        if (created != null) cacheCity(created);
        rebuildCityListCache();
        return created;
    }

    /** ✏️ 도시 수정 */
    public CityDTO update(CityDTO dto) {
        String url = apiBaseUrl + "/api/cities/" + dto.getId();
        restTemplate.put(url, dto);
        cacheCity(dto);
        rebuildCityListCache();
        return dto;
    }

    /** 🗑️ 도시 삭제 */
    public void delete(Long id) {
        String url = apiBaseUrl + "/api/cities/" + id;
        restTemplate.delete(url);
        rebuildCityListCache();
    }

    /** 🧠 Redis 캐시 개별 저장 */
    private void cacheCity(CityDTO dto) {
        try {
            String key = CITY_KEY_PREFIX + dto.getCityName();
            redisTemplate.opsForValue().set(key, dto, Duration.ofHours(24));
        } catch (Exception e) {
            System.err.println("⚠️ [AdminCityService] 캐시 실패: " + e.getMessage());
        }
    }

    /** 🔄 Redis 전체 city:list 재빌드 */
    public void rebuildCityListCache() {
        try {
            String url = apiBaseUrl + "/api/cities";
            CityDTO[] all = restTemplate.getForObject(url, CityDTO[].class);
            if (all != null) {
                String json = objectMapper.writeValueAsString(all);
                stringRedisTemplate.opsForValue().set(CITY_LIST_KEY, json, Duration.ofHours(24));
            }
        } catch (Exception e) {
            System.err.println("❌ [AdminCityService] city:list 재빌드 실패: " + e.getMessage());
        }
    }
}
