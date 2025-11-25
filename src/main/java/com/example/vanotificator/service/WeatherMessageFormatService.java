package com.example.vanotificator.service;

import com.example.vanotificator.dto.ForecastDto;
import com.example.vanotificator.util.WeatherUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

@Slf4j
@Service
public class WeatherMessageFormatService {

    public String formatCurrentForecast(ForecastDto forecast) {
        DayOfWeek dayOfWeekToday =
                forecast.getDayOfWeek();
        String dayOfWeekFormatted =
                dayOfWeekToday.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String cityName =
                forecast.getCityName();
        double temperature =
                forecast.getTemperature();
        String temperatureLvl =
                WeatherUtil.getTemperatureLvl(temperature)
                        .name()
                        .toLowerCase();
        String precipitationProbability =
                WeatherUtil.getPrecipitationProbability(forecast.getPop())
                        .name()
                        .toLowerCase();
        String windLvl =
                WeatherUtil.getWindLvl(forecast.getWindSpeed())
                        .name()
                        .toLowerCase();
        String cloudCoveredge =
                WeatherUtil.getCloudCoveredge(forecast.getClouds())
                        .name()
                        .toLowerCase();

        if (cloudCoveredge.contains("_")) {
            cloudCoveredge = cloudCoveredge.replace("_", " ");
        }

        StringBuilder sb = new StringBuilder();

        sb.append("Hello, ").append(cityName).append("!\n")
                .append("Today is ")
                .append(dayOfWeekFormatted)
                .append(", ")
                .append(forecast.getDate()).append("\n")
                .append(String.format(
                        "Current temperature: %.1f°C, feeling %s\n",
                        temperature, temperatureLvl))
                .append("Precipitation probability: ")
                .append(precipitationProbability)
                .append("\n")
                .append("Sky: ")
                .append(cloudCoveredge)
                .append("\n");

        if (!precipitationProbability.equals("no")) {
            String precipitationLvl =
                    WeatherUtil.getPrecipitationLvl(forecast.getPrecipitation())
                            .name()
                            .toLowerCase();
            sb.append("Precipitation level: ")
                    .append(precipitationLvl)
                    .append("\n");
        }

        sb.append("Wind: ")
                .append(windLvl);
        return sb.toString();
    }
}
