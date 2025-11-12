package com.example.vanotificator.repository;

import com.example.vanotificator.model.City;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    @EntityGraph(attributePaths = {"coordinates"})
    Optional<City> findByName(String name);

    @Query("SELECT c FROM City c JOIN c.coordinates coord " +
            "WHERE coord.lat = :lat AND coord.lon = :lon")
    Optional<City> findByCoordinates(@Param("lat") double lat,
                                     @Param("lon") double lon);
}
