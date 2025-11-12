package com.example.vanotificator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GeoResponseDto {
    @JsonProperty("name")
    private String cityName;

    private double lat;

    private double lon;

    private String country;

    private String state;
}
