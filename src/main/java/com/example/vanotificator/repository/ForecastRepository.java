package com.example.vanotificator.repository;

import com.example.vanotificator.model.Forecast;
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

    void deleteAllByCity_NameAndDateBetween(
            String cityName, LocalDate startDate, LocalDate endDate);

    Optional<Forecast> findFirstByCity_NameAndDateGreaterThanEqualOrderByDateAscTimeAsc(
            String cityName,
            LocalDate date
    );

        @Modifying
        @Query("DELETE FROM Forecast f " +
                "WHERE f.city.name IN :cityNames " +
                "AND f.date BETWEEN :start AND :end")
        void deleteAllByCityInAndDateBetween(@Param("cityNames") List<String> cityNames,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    List<Forecast> findByCity_NameAndDate(String cityName, LocalDate date);

    List<Forecast> findByCity_NameAndDateBefore(String cityName, LocalDate date);

    void deleteAllByDateBefore(LocalDate dateBefore);
}
