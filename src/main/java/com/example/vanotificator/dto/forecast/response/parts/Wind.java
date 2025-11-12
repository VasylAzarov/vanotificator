package com.example.vanotificator.dto.forecast.response.parts;

import lombok.Data;

@Data
public class Wind {
    private double speed;

    private int deg;

    private double gust;
}
