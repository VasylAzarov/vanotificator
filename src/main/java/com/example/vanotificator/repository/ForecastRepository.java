package com.example.vanotificator.repository;

import com.example.vanotificator.model.Forecast;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ForecastRepository extends JpaRepository<Forecast, Integer> {

    void deleteAllByCity_NameIgnoreCaseAndDateBetween(
            String cityName, LocalDate startDate, LocalDate endDate);

    @Query("SELECT f FROM Forecast f " +
            "WHERE LOWER(f.city.name) = LOWER(:cityName) " +
            "AND f.date >= :date " +
            "ORDER BY f.date ASC, f.time ASC")
    List<Forecast> findForecastsAfterDate(
            @Param("cityName") String cityName,
            @Param("date") LocalDate date,
            Pageable pageable);

    @Modifying
    @Query("DELETE FROM Forecast f " +
            "WHERE LOWER(f.city.name) IN :cityNames " +
            "AND f.date BETWEEN :start AND :end")
    void deleteAllByCityInIgnoreCaseAndDateBetween(
            @Param("cityNames") List<String> cityNames,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    List<Forecast> findByCity_NameIgnoreCaseAndDate(String cityName, LocalDate date);

    List<Forecast> findByCity_NameIgnoreCaseAndDateBefore(String cityName, LocalDate date);

    void deleteAllByDateBefore(LocalDate dateBefore);
}
