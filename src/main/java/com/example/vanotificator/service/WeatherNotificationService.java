package com.example.vanotificator.service;

import com.example.vanotificator.dto.ForecastDto;
import com.example.vanotificator.dto.UserDataRequestDto;
import org.springframework.stereotype.Service;


@Service
public class WeatherNotificationService {

    private final UserCityResolverService cityResolverService;
    private final ForecastResolverService forecastResolverService;
    private final WeatherMessageFormatter formatter;

    public WeatherNotificationService(UserCityResolverService cityResolverService,
                                      ForecastResolverService forecastResolverService,
                                      WeatherMessageFormatter formatter) {
        this.cityResolverService = cityResolverService;
        this.forecastResolverService = forecastResolverService;
        this.formatter = formatter;
    }

    public String buildNearestWeatherMessage(UserDataRequestDto dto) {
        String cityName = cityResolverService.resolveCityName(dto);

        ForecastDto forecast = forecastResolverService.getNearestForecast(cityName);

        return formatter.formatCurrentForecast(forecast);
    }
}

