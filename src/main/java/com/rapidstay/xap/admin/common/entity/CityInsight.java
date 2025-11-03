package com.rapidstay.xap.admin.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "city_insight")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor // ✅ 여기에 추가
public class CityInsight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cityName;
    private String cityNameKr;
    private String country;

    @Column(length = 1000)
    private String airports;      // 콤마로 구분된 문자열

    @Column(length = 2000)
    private String attractions;   // 콤마로 구분된 문자열

    @Column
    private Double lat;           // 위도

    @Column
    private Double lon;           // 경도

    // Lombok Getter/Setter
    public String getCityName() { return cityName; }
    public String getCityNameKr() { return cityNameKr; }
    public String getCountry() { return country; }
    public String getAirports() { return airports; }
    public String getAttractions() { return attractions; }
    public Double getLat() { return lat; }
    public Double getLon() { return lon; }

    public void setCityName(String cityName) { this.cityName = cityName; }
    public void setCityNameKr(String cityNameKr) { this.cityNameKr = cityNameKr; }
    public void setCountry(String country) { this.country = country; }
    public void setAirports(String airports) { this.airports = airports; }
    public void setAttractions(String attractions) { this.attractions = attractions; }
    public void setLat(Double lat) { this.lat = lat; }
    public void setLon(Double lon) { this.lon = lon; }
}