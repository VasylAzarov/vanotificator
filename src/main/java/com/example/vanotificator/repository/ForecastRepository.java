package com.example.vanotificator.repository;

import com.example.vanotificator.model.Forecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ForecastRepository extends JpaRepository<Forecast, Integer> {

    void deleteAllByCity_NameAndDateBetween(
            String cityName, LocalDate startDate, LocalDate endDate);

    Optional<Forecast> findFirstByCity_NameAndDateGreaterThanEqualOrderByDateAscTimeAsc(
            String cityName,
            LocalDate date
    );

    List<Forecast> findByCity_NameAndDate(String cityName, LocalDate date);

    List<Forecast> findByCity_NameAndDateBefore(String cityName, LocalDate date);
}
