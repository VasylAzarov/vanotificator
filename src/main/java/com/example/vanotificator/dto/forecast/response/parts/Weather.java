package com.example.vanotificator.dto.forecast.response.parts;

import lombok.Data;

@Data
public class Weather {
    private int id;

    private String main;

    private String description;

    private String icon;
}
