package com.rapidstay.xap.admin.controller;

import com.rapidstay.xap.common.dto.CityDTO; // ✅ common 모듈 dto
import com.rapidstay.xap.admin.service.AdminCityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/cities")
@RequiredArgsConstructor
public class AdminCityController {

    private final AdminCityService adminCityService;

    @GetMapping
    public List<CityDTO> list(@RequestParam(required = false) String query) {
        return adminCityService.list(query);
    }

    @PostMapping
    public CityDTO create(@RequestBody CityDTO dto) {
        return adminCityService.create(dto);
    }

    @PutMapping("/{id}")
    public CityDTO update(@PathVariable Long id, @RequestBody CityDTO dto) {
        dto.setId(id);
        return adminCityService.update(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        adminCityService.delete(id);
    }
}
