package com.example.vanotificator.repository;

import com.example.vanotificator.model.CityLocaleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityLocaleNameRepository extends JpaRepository<CityLocaleName, Long> {

    @Query("SELECT cln.city.name FROM CityLocaleName cln " +
            "WHERE cln.locale = :locale AND cln.city.name IN :cityNames")
    List<String> findCityNamesByLocaleAndCityNames(
            @Param("locale") String locale,
            @Param("cityNames") List<String> cityNames);
}
