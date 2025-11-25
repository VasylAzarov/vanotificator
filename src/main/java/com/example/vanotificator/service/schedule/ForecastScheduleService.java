package com.example.vanotificator.service.schedule;

import com.example.vanotificator.service.ForecastService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
public class ForecastScheduleService {
    private static final Logger log = LoggerFactory.getLogger(ForecastScheduleService.class);

    private final ForecastService forecastService;

    public ForecastScheduleService(ForecastService forecastService) {
        this.forecastService = forecastService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    void deleteOldForecasts()  {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        forecastService.removeOldForecasts(today);
        log.info("Old forecasts have been deleted");
    }

    @Scheduled(cron = "0 0 0 * * MON")
    void updateForecasts() {
        forecastService.generateForecasts();
        log.info("Forecasts have been updated");

    }
}
