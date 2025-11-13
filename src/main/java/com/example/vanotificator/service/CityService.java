package com.example.vanotificator.service;

import com.example.vanotificator.dto.CityCreateDto;
import com.example.vanotificator.dto.CityDto;
import com.example.vanotificator.exeption.CityNotFoundException;
import com.example.vanotificator.mapper.CityMapper;
import com.example.vanotificator.model.City;
import com.example.vanotificator.model.Coordinates;
import com.example.vanotificator.repository.CityRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CityService {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    public CityService(CityRepository cityRepository,
                       CityMapper cityMapper) {
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
    }

    public CityDto getCityByName(String cityName) {
        City city = cityRepository.findByName(cityName.toLowerCase())
                .orElseThrow(() -> new CityNotFoundException(
                        "City by name: [" + cityName + "] not found!"
                ));
        return cityMapper.toDto(city);
    }

    public City getCityEntityByName(String cityName) {
        return cityRepository.findByName(cityName.toLowerCase())
                .orElseThrow(() -> new CityNotFoundException(
                        "City by name: [" + cityName + "] not found!"
                ));
    }

    public CityDto getCityByCoordinates(double lat, double lon) {
        City city = cityRepository.findByCoordinates(lat, lon)
                .orElseThrow(() -> new CityNotFoundException(
                        "City by coordinates: [" + lat + "][" + lon + "] not found!"
                ));
        return cityMapper.toDto(city);
    }

    public CityDto createCity(CityCreateDto cityCreateDto) {
        City city = new City();

        double lat = roundCoordinate(cityCreateDto.getLat());
        double lon = roundCoordinate(cityCreateDto.getLon());

        Coordinates coordinates = new Coordinates();
        coordinates.setLat(lat);
        coordinates.setLon(lon);
        coordinates.setCity(city);

        city.setName(cityCreateDto.getName().toLowerCase());
        city.getCoordinates().add(coordinates);

        cityRepository.save(city);
        return cityMapper.toDto(city);
    }

    public CityDto updateCityCoordinates(String cityName,
                                         double lat,
                                         double lon) {
        lat = roundCoordinate(lat);
        lon = roundCoordinate(lon);

        City city = cityRepository.findByName(cityName)
                .orElseThrow(() -> new CityNotFoundException(
                        "City by name: [" + cityName + "] not found!"));

        Coordinates coordinates = new Coordinates();
        coordinates.setLat(lat);
        coordinates.setLon(lon);
        coordinates.setCity(city);

        city.getCoordinates().add(coordinates);

        return cityMapper.toDto(cityRepository.save(city));
    }

    private double roundCoordinate(double coordinate) {
        return Math.round(coordinate * 1e2) / 1e2;
    }
}
