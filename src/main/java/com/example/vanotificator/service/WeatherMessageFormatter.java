package com.example.vanotificator.service;

import com.example.vanotificator.dto.ForecastDto;
import com.example.vanotificator.util.WeatherUtil;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

@Service
public class WeatherMessageFormatter {

    private final WeatherUtil weatherUtil;

    public WeatherMessageFormatter(WeatherUtil weatherUtil) {
        this.weatherUtil = weatherUtil;
    }

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
                weatherUtil.getTemperatureLvl(temperature)
                        .name()
                        .toLowerCase();
        String precipitationProbability =
                weatherUtil.getPrecipitationProbability(forecast.getPop())
                        .name()
                        .toLowerCase();
        String windLvl =
                weatherUtil.getWindLvl(forecast.getWindSpeed())
                        .name()
                        .toLowerCase();
        String cloudCoveredge =
                weatherUtil.getCloudCoveredge(forecast.getClouds())
                        .name()
                        .toLowerCase();
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
                    weatherUtil.getPrecipitationLvl(forecast.getPrecipitation())
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
