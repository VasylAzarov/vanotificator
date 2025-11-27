package com.example.vanotificator.service;

import com.example.vanotificator.dto.ForecastDto;
import com.example.vanotificator.dto.WeatherResponseDto;
import com.example.vanotificator.event.CitiesInitializedEvent;
import com.example.vanotificator.exeption.ForecastNotFoundException;
import com.example.vanotificator.mapper.ForecastMapper;
import com.example.vanotificator.model.City;
import com.example.vanotificator.model.Forecast;
import com.example.vanotificator.repository.ForecastRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ForecastService {

    private static final Logger log = LoggerFactory.getLogger(ForecastService.class);

    private final ForecastRepository forecastRepository;
    private final ForecastMapper forecastMapper;
    private final CityService cityService;
    private final RequestService requestService;

    public ForecastService(
            ForecastMapper forecastMapper,
            ForecastRepository forecastRepository,
            CityService cityService,
            RequestService requestService
    ) {
        this.forecastRepository = forecastRepository;
        this.forecastMapper = forecastMapper;
        this.cityService = cityService;
        this.requestService = requestService;
    }

    @EventListener
    public void onCitiesInitialized(CitiesInitializedEvent event) {
        log.info("Start updating forecasts for cities...");
        generateForecasts();
        log.info("Updating forecasts for cities is complete!");

    }

    public void updateForecasts(List<WeatherResponseDto> weatherResponseDtos) {
        List<City> cities = prepareCity(weatherResponseDtos);
        List<Forecast> forecasts = buildForecastsInUTC(weatherResponseDtos, cities);
        replaceOldForecasts(cities, forecasts);
    }

    public ForecastDto getNearestForecastByCityName(String cityName) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        List<Forecast> forecasts = forecastRepository
                .findForecastsAfterDate(cityName,
                        today,
                        PageRequest.of(0, 1));
        if  (forecasts.isEmpty()) {
            throw new ForecastNotFoundException(
                    "Forecast by city name: [" + cityName + "] not found!"
            );
        } else {
            return forecastMapper.toDto(forecasts.get(0));
        }
    }

    public List<ForecastDto> getDailyForecastByCityName(String cityName) {
        LocalDate todayUtc = LocalDate.now(ZoneOffset.UTC);
        return forecastMapper.toDto(
                forecastRepository.findByCity_NameIgnoreCaseAndDate(cityName, todayUtc)
        );
    }

    public List<ForecastDto> getWeeklyForecastByCityName(String cityName) {
        LocalDate until = LocalDate.now(ZoneOffset.UTC).plusDays(7);
        return forecastMapper.toDto(
                forecastRepository.findByCity_NameIgnoreCaseAndDateBefore(cityName, until)
        );
    }

    public void removeOldForecasts(LocalDate today) {
        forecastRepository.deleteAllByDateBefore(today);
    }

    private List<City> prepareCity(List<WeatherResponseDto> dtos) {
        Map<String, Integer> timezones = dtos.stream().collect(Collectors.toMap(
                item -> item.getCity().getName(),
                item -> item.getCity().getTimezone()
        ));
        List<String> names = new ArrayList<>(timezones.keySet());
        List<City> cities = cityService.getCityEntityByNamesIn(names);
        for (City city : cities) {
            Integer tz = timezones.get(city.getName());
            if (tz != null) city.setTimezone(tz);
        }
        return cities;
    }

    private List<Forecast> buildForecastsInUTC(List<WeatherResponseDto> dtos, List<City> cities) {
        Map<String, City> cityMap = cities.stream()
                .collect(Collectors.toMap(c -> c.getName(), c -> c));

        List<Forecast> all = new ArrayList<>();
        for (WeatherResponseDto dto : dtos) {
            City city = cityMap.get(dto.getCity().getName());
            if (city == null) continue;

            ZoneOffset offset = ZoneOffset.ofTotalSeconds(dto.getCity().getTimezone());
            List<Forecast> forecasts = forecastMapper.toEntity(dto);

            for (Forecast f : forecasts) {
                LocalDateTime local = LocalDateTime.of(f.getDate(), f.getTime());
                LocalDateTime utc = local.atOffset(offset)
                        .withOffsetSameInstant(ZoneOffset.UTC)
                        .toLocalDateTime();

                f.setDate(utc.toLocalDate());
                f.setTime(utc.toLocalTime());
                f.setCity(city);
            }
            all.addAll(forecasts);
        }
        return all;
    }

    private void replaceOldForecasts(List<City> cities, List<Forecast> forecasts) {
        if (forecasts.isEmpty() || cities.isEmpty()) return;

        forecasts.sort((a, b) -> {
            int cmp = a.getDate().compareTo(b.getDate());
            return (cmp != 0) ? cmp : a.getTime().compareTo(b.getTime());
        });

        LocalDate start = forecasts.get(0).getDate();
        LocalDate end = forecasts.get(forecasts.size() - 1).getDate();

        List<String> cityNames = cities.stream()
                .map(City::getName)
                .toList();

        forecastRepository.deleteAllByCityInIgnoreCaseAndDateBetween(cityNames, start, end);

        forecastRepository.saveAll(forecasts);
    }

    public void generateForecasts() {
        LocalDate todayUtc = LocalDate.now(ZoneOffset.UTC);
        List<String> cityNames = cityService.getCitiesNamesWithoutForecastForDate(todayUtc);

        List<WeatherResponseDto> responses = new ArrayList<>();
        for (String cityName : cityNames) {
            try {
                WeatherResponseDto r = requestService.getWeather(cityName);
                r.getCity().setName(cityName);
                responses.add(r);
            } catch (Exception e) {
                log.error("Failed to fetch forecast for city {}: {}",
                        cityName,
                        e.getMessage());
            }
        }
        if (!responses.isEmpty()) updateForecasts(responses);
    }
}

