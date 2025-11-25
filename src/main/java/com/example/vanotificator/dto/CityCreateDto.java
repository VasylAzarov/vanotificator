package com.example.vanotificator.dto;

import lombok.Data;

@Data
public class CityCreateDto {
    private String name;

    private double lat;

    private double lon;

    private int timezone = 0;
}
