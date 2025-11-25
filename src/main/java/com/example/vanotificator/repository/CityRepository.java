package com.example.vanotificator.repository;

import com.example.vanotificator.model.City;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    @EntityGraph(attributePaths = {"coordinates"})
    Optional<City> findByName(String name);

    @EntityGraph(attributePaths = {"coordinates"})
    List<City> findAllByNameIn(Set<String> names);

    @Query("SELECT c.name FROM City c")
    List<String> findAllCityNames();

    @Query("SELECT c FROM City c JOIN c.coordinates coord " +
            "WHERE coord.lat = :lat AND coord.lon = :lon")
    Optional<City> findByCoordinates(@Param("lat") double lat,
                                     @Param("lon") double lon);

    @Query("SELECT c.name FROM City c WHERE NOT EXISTS (" +
            "SELECT f FROM Forecast f WHERE f.city = c AND f.date = :today)")
    List<String> findCityNamesWithoutForecastForDate(@Param("today") LocalDate today);


    @Query("select c.name from City c where lower(c.name) like lower(concat('%', :namePart, '%'))")
    List<String> findCityNames(@Param("namePart") String namePart);}
