package com.rapidstay.xap.admin.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapidstay.xap.admin.common.dto.CityDTO;
import com.rapidstay.xap.admin.common.entity.CityInsight;
import com.rapidstay.xap.admin.common.repository.CityInsightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminCityService {

    private final RedisTemplate<String, CityDTO> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final CityInsightRepository cityInsightRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

        // ✅ Redis 비어있으면 DB fallback
        return cityInsightRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /** 🏗️ 도시 생성 */
    public CityDTO create(CityDTO dto) {
        CityInsight entity = cityInsightRepository.save(toEntity(dto));
        CityDTO saved = toDto(entity);
        cacheCity(saved);
        rebuildCityListCache();
        return saved;
    }

    /** ✏️ 도시 수정 */
    public CityDTO update(CityDTO dto) {
        CityInsight entity = cityInsightRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("도시를 찾을 수 없습니다."));
        entity.setCityName(dto.getCityName());
        entity.setCityNameKr(dto.getCityNameKr());
        entity.setCountry(dto.getCountry());
        entity.setLat(dto.getLat());
        entity.setLon(dto.getLon());
        entity.setAirports(dto.getAirports() == null ? "" : String.join(",", dto.getAirports()));
        entity.setAttractions(dto.getAttractions() == null ? "" : String.join(",", dto.getAttractions()));

        cityInsightRepository.save(entity);
        CityDTO updated = toDto(entity);
        cacheCity(updated);
        rebuildCityListCache();
        return updated;
    }

    /** 🗑️ 도시 삭제 */
    public void delete(Long id) {
        cityInsightRepository.deleteById(id);
        rebuildCityListCache();
    }

    /** 🧠 Redis 캐시 개별 저장 */
    private void cacheCity(CityDTO dto) {
        try {
            String key = CITY_KEY_PREFIX + dto.getCityName().toLowerCase();
            redisTemplate.opsForValue().set(key, dto, Duration.ofHours(24));
        } catch (Exception e) {
            System.err.println("⚠️ [AdminCityService] 캐시 실패: " + e.getMessage());
        }
    }

    /** 🔄 Redis 전체 city:list 재빌드 */
    public void rebuildCityListCache() {
        try {
            List<CityDTO> all = cityInsightRepository.findAll().stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            String json = objectMapper.writeValueAsString(all);
            stringRedisTemplate.opsForValue().set(CITY_LIST_KEY, json, Duration.ofHours(24));
            System.out.println("🧠 [AdminCityService] city:list 캐시 재빌드 완료 (" + all.size() + "건)");
        } catch (Exception e) {
            System.err.println("❌ [AdminCityService] city:list 재빌드 실패: " + e.getMessage());
        }
    }

    /** 🔁 Entity ↔ DTO 변환 */
    private CityDTO toDto(CityInsight e) {
        return CityDTO.builder()
                .id(e.getId())
                .cityName(e.getCityName())
                .cityNameKr(e.getCityNameKr())
                .country(e.getCountry())
                .airports(split(e.getAirports()))
                .attractions(split(e.getAttractions()))
                .lat(e.getLat())
                .lon(e.getLon())
                .build();
    }

    private CityInsight toEntity(CityDTO dto) {
        return CityInsight.builder()
                .id(dto.getId())
                .cityName(dto.getCityName())
                .cityNameKr(dto.getCityNameKr())
                .country(dto.getCountry())
                .airports(dto.getAirports() == null ? "" : String.join(",", dto.getAirports()))
                .attractions(dto.getAttractions() == null ? "" : String.join(",", dto.getAttractions()))
                .lat(dto.getLat())
                .lon(dto.getLon())
                .build();
    }

    private List<String> split(String s) {
        if (s == null || s.isBlank()) return List.of();
        return Arrays.stream(s.split(","))
                .map(String::trim)
                .filter(str -> !str.isEmpty())
                .toList();
    }
}
