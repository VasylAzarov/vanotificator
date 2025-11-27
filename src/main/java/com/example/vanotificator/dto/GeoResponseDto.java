package com.example.vanotificator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class GeoResponseDto {
    @JsonProperty("name")
    private String cityName;

    @JsonProperty("local_names")
    private Map<String, String> localNames;

    private double lat;

    private double lon;

    private String country;

    private String state;
}
