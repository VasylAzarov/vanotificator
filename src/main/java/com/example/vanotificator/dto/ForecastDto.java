package com.example.vanotificator.dto;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ForecastDto {

    private LocalDate date;

    private LocalTime time;

    private DayOfWeek dayOfWeek;

    private double temperature;

    private double precipitation;

    private double pop;

    private double windSpeed;

    private int clouds;

    private String cityName;
}
