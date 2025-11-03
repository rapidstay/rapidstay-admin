package com.rapidstay.xap.admin.common.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@JsonDeserialize(builder = CityDTO.CityDTOBuilder.class)
public class CityDTO {
    private Long id;
    private String cityName;
    private String cityNameKr;
    private String country;
    private List<String> airports;
    private List<String> attractions;
    private Double lat;
    private Double lon;
    private String error;

    @JsonPOJOBuilder(withPrefix = "")
    public static class CityDTOBuilder {}
}

