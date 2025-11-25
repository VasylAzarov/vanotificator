package com.example.vanotificator.service;

import com.example.vanotificator.dto.CityCreateDto;
import com.example.vanotificator.dto.CityDto;
import com.example.vanotificator.events.CitiesInitializedEvent;
import com.example.vanotificator.exeption.CityAlreadyExistsException;
import com.example.vanotificator.exeption.CityNotFoundException;
import com.example.vanotificator.mapper.CityMapper;
import com.example.vanotificator.model.City;
import com.example.vanotificator.model.Coordinates;
import com.example.vanotificator.repository.CityRepository;
import com.example.vanotificator.util.CityNameNormalizer;
import com.example.vanotificator.util.CityUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CityService {

    private static final Logger log = LoggerFactory.getLogger(CityService.class);

    private final CityReaderService cityReaderService;
    private final CityRepository cityRepository;
    private final CityMapper cityMapper;
    private final ApplicationEventPublisher publisher;
    private final CityNameNormalizer normalizer;

    public CityService(
            CityReaderService cityReaderService,
            CityRepository cityRepository,
            CityMapper cityMapper,
            ApplicationEventPublisher publisher,
            CityNameNormalizer normalizer
    ) {
        this.cityReaderService = cityReaderService;
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
        this.publisher = publisher;
        this.normalizer = normalizer;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("Start cities initialization...");
        updateCitiesFromFile();
        log.info("Cities initialization is complete!");
        publisher.publishEvent(new CitiesInitializedEvent(this));
    }

    public CityDto getCityByName(String rawName) {
        String normalized = normalizer.normalize(rawName);
        City city = cityRepository.findByName(normalized)
                .orElseThrow(() -> new CityNotFoundException(
                        "City by name: [" + rawName + "] not found!"
                ));
        return cityMapper.toDto(city);
    }

    public City getCityEntityByName(String rawName) {
        String normalized = normalizer.normalize(rawName);
        return cityRepository.findByName(normalized)
                .orElseThrow(() -> new CityNotFoundException(
                        "City by name: [" + rawName + "] not found!"
                ));
    }

    public CityDto getCityByCoordinates(double lat, double lon) {
        double latitude = CityUtil.roundCoordinate(lat);
        double longitude = CityUtil.roundCoordinate(lon);
        City city = cityRepository.findByCoordinates(latitude, longitude)
                .orElseThrow(() -> new CityNotFoundException(
                        "City by coordinates: [" + latitude + "][" + longitude + "] not found!"
                ));
        return cityMapper.toDto(city);
    }

    public CityDto createCity(CityCreateDto cityCreateDto) {
        try {
            City city = cityRepository.save(cityMapper.fromCreateDto(cityCreateDto));
            return cityMapper.toDto(city);
        } catch (DataIntegrityViolationException e) {
            throw new CityAlreadyExistsException(
                    "City with name [" + cityCreateDto.getName() + "] already exists!"
            );
        }
    }

    public CityDto updateCityCoordinates(String rawName, double lat, double lon) {
        City city = getCityEntityByName(rawName);

        Coordinates coordinates = new Coordinates();
        coordinates.setLat(CityUtil.roundCoordinate(lat));
        coordinates.setLon(CityUtil.roundCoordinate(lon));
        coordinates.setCity(city);

        city.getCoordinates().add(coordinates);
        return cityMapper.toDto(cityRepository.save(city));
    }

    public List<City> getCityEntityByNamesIn(Collection<String> cityNames) {
        if (cityNames == null || cityNames.isEmpty()) {
            return List.of();
        }

        Set<String> normalized = cityNames.stream()
                .filter(Objects::nonNull)
                .map(normalizer::normalize)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        return cityRepository.findAllByNameIn(normalized);
    }

    public List<String> getCitiesNamesWithoutForecastForDate(LocalDate dateUtc) {
        return cityRepository.findCityNamesWithoutForecastForDate(dateUtc);
    }

    public List<String> getCitiesNamesByNamePart(String namePart) {
        return cityRepository.findCityNames(namePart);
    }

    private void updateCitiesFromFile() {
        List<CityCreateDto> dtos = cityReaderService.readCityFromFile();

        Set<String> existing = cityRepository.findAllCityNames().stream()
                .map(String::toLowerCase).collect(Collectors.toSet());

        List<City> toInsert = dtos.stream()
                .filter(dto -> !existing.contains(dto.getName().toLowerCase()))
                .map(dto -> {
                    City city = new City();
                    city.setName(dto.getName().toLowerCase());
                    city.setTimezone(dto.getTimezone());

                    Coordinates coordinates = new Coordinates();
                    coordinates.setLat(dto.getLat());
                    coordinates.setLon(dto.getLon());
                    coordinates.setCity(city);

                    city.getCoordinates().add(coordinates);
                    return city;
                })
                .toList();

        if (!toInsert.isEmpty()) {
            cityRepository.saveAll(toInsert);
        }
    }
}
