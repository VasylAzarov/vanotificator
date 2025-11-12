package com.example.vanotificator.service;

import com.example.vanotificator.dto.ForecastDto;
import com.example.vanotificator.dto.WeatherResponseDto;
import com.example.vanotificator.exeption.ForecastNotFoundException;
import com.example.vanotificator.mapper.ForecastMapper;
import com.example.vanotificator.model.City;
import com.example.vanotificator.model.Forecast;
import com.example.vanotificator.repository.ForecastRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Transactional
public class ForecastService {

    private final ForecastRepository forecastRepository;
    private final ForecastMapper forecastMapper;
    private final CityService cityService;

    public ForecastService(ForecastMapper forecastMapper,
                           ForecastRepository forecastRepository,
                           CityService cityService) {
        this.forecastRepository = forecastRepository;
        this.forecastMapper = forecastMapper;
        this.cityService = cityService;
    }

    public void updateForecasts(WeatherResponseDto weatherResponseDto) {
        City city = prepareCity(weatherResponseDto);
        List<Forecast> forecasts = buildForecastsInUTC(weatherResponseDto, city);
        replaceOldForecasts(city, forecasts);
    }

    public ForecastDto getNearestForecastByCityName(String cityName) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        Forecast forecast = forecastRepository
                .findFirstByCity_NameAndDateGreaterThanEqualOrderByDateAscTimeAsc(
                        cityName,
                        today)
                .orElseThrow(() -> new ForecastNotFoundException(
                        "Forecast by city name: [" + cityName + "] not found!"
                ));

        return forecastMapper.toDto(forecast);
    }

    public List<ForecastDto> getDailyForecastByCityName(String cityName) {
        LocalDate todayUtc = LocalDate.now(ZoneOffset.UTC);
        List<Forecast> forecasts = forecastRepository
                .findByCity_NameAndDate(cityName, todayUtc);
        return forecastMapper.toDto(forecasts);
    }

    public List<ForecastDto> getWeeklyForecastByCityName(String cityName) {
        LocalDate date = LocalDate.now(ZoneOffset.UTC).plusDays(7);
        List<Forecast> forecasts = forecastRepository
                .findByCity_NameAndDateBefore(cityName, date);
        return forecastMapper.toDto(forecasts);
    }

    private City prepareCity(WeatherResponseDto dto) {
        City city = cityService.getCityEntityByName(dto
                .getCity()
                .getName()
                .toLowerCase());
        city.setTimezone(dto
                .getCity()
                .getTimezone());
        return city;
    }

    private List<Forecast> buildForecastsInUTC(WeatherResponseDto dto, City city) {
        int timezoneOffset = dto.getCity().getTimezone();
        List<Forecast> forecasts = forecastMapper.toEntity(dto);
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(timezoneOffset);

        for (Forecast forecast : forecasts) {

            LocalDateTime localDateTime = LocalDateTime.of(
                    forecast.getDate(),
                    forecast.getTime());

            ZonedDateTime utc = localDateTime
                    .atOffset(offset)
                    .withOffsetSameInstant(ZoneOffset.UTC)
                    .toZonedDateTime();

            forecast.setDate(utc.toLocalDate());
            forecast.setTime(utc.toLocalTime());
            forecast.setCity(city);
        }
        return forecasts;
    }

    private void replaceOldForecasts(City city, List<Forecast> forecasts) {
        if (forecasts.isEmpty()) return;

        LocalDate start = forecasts.get(0).getDate();
        LocalDate end = forecasts.get(forecasts.size() - 1).getDate();

        forecastRepository.deleteAllByCity_NameAndDateBetween(
                city.getName(),
                start,
                end);
        forecastRepository.saveAll(forecasts);
    }

}
