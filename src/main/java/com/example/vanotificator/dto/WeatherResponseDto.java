package com.example.vanotificator.dto;

import com.example.vanotificator.dto.forecast.response.parts.City;
import com.example.vanotificator.dto.forecast.response.parts.WeatherListItem;
import lombok.Data;

import java.util.List;

@Data
public class WeatherResponseDto {
    private String cod;

    private int message;

    private int cnt;

    private List<WeatherListItem> list;

    private City city;

}

