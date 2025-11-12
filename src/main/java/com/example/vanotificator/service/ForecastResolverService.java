package com.example.vanotificator.service;

import com.example.vanotificator.dto.ForecastDto;
import com.example.vanotificator.dto.WeatherResponseDto;
import com.example.vanotificator.exeption.ForecastNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ForecastResolverService {

    private final RequestService requestService;
    private final ForecastService forecastService;

    public ForecastResolverService(RequestService requestService,
                                   ForecastService forecastService) {
        this.requestService = requestService;
        this.forecastService = forecastService;
    }

    public ForecastDto getNearestForecast(String cityName) {
        ForecastDto forecastDto;
        try {
            forecastDto = forecastService
                    .getNearestForecastByCityName(cityName);
        } catch (ForecastNotFoundException e) {
            WeatherResponseDto weatherResponseDto
                    = requestService.getWeather(cityName);
            forecastService.updateForecasts(weatherResponseDto);
            forecastDto = forecastService
                    .getNearestForecastByCityName(cityName);
        }
        return forecastDto;
    }
}
