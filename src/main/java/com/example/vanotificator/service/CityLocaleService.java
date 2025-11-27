package com.example.vanotificator.service;

import com.example.vanotificator.dto.CityLocaleNameCreateDto;
import com.example.vanotificator.dto.CityLocaleNameFromFileDto;
import com.example.vanotificator.event.CitiesInitializedEvent;
import com.example.vanotificator.exeption.CityNotFoundException;
import com.example.vanotificator.mapper.CityLocaleMapper;
import com.example.vanotificator.model.City;
import com.example.vanotificator.model.CityLocaleName;
import com.example.vanotificator.repository.CityLocaleNameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CityLocaleService {

    private static final Logger log = LoggerFactory.getLogger(CityLocaleService.class);

    private final CityLocaleNameRepository cityLocaleNameRepository;
    private final CityService cityService;
    private final FileReaderService fileReaderService;
    private final CityLocaleMapper cityLocaleMapper;

    public CityLocaleService(CityLocaleNameRepository cityLocaleNameRepository,
                             CityService cityService,
                             FileReaderService fileReaderService,
                             CityLocaleMapper cityLocaleMapper) {
        this.cityLocaleNameRepository = cityLocaleNameRepository;
        this.cityService = cityService;
        this.fileReaderService = fileReaderService;
        this.cityLocaleMapper = cityLocaleMapper;
    }

    @EventListener
    public void onCitiesInitialized(CitiesInitializedEvent event) {
        log.info("Start updating city locales...");
        generateLocales();
        log.info("Updating city locales is complete!");
    }

    private void generateLocales() {
        List<CityLocaleNameCreateDto> createDtos = getListOfNewDtos();

        List<String> existingCitiesNames = createDtos.stream()
                .map(CityLocaleNameCreateDto::getCityName)
                .distinct()
                .toList();

        List<City> cities = cityService.getCityEntityByNamesIn(existingCitiesNames);
        Map<String, City> cityMap = cities.stream()
                .collect(Collectors.toMap(City::getName, c -> c));

        List<CityLocaleName> entities = new ArrayList<>();
        for (CityLocaleNameCreateDto createDto : createDtos) {
            City city = cityMap.get(createDto.getCityName());
            if (city == null) {
                throw new CityNotFoundException(
                        "City not found in list of loaded cities: " + createDto.getCityName());
            }

            CityLocaleName cityLocaleName = new CityLocaleName();
            cityLocaleName.setLocale(createDto.getLocale());
            cityLocaleName.setLocaleName(createDto.getName());
            cityLocaleName.setCity(city);

            entities.add(cityLocaleName);
        }

        if (!entities.isEmpty()) {
            cityLocaleNameRepository.saveAll(entities);
        }
    }

    private List<CityLocaleNameCreateDto> getListOfNewDtos() {
        List<CityLocaleNameFromFileDto> fileDtos = fileReaderService.readCityLocaleFromFile();
        List<CityLocaleNameCreateDto> createDtos = cityLocaleMapper.toCreateDto(fileDtos);

        List<String> locales = createDtos.stream().map(CityLocaleNameCreateDto::getLocale).distinct().toList();
        List<String> cityNames = createDtos.stream().map(CityLocaleNameCreateDto::getCityName).distinct().toList();

        Map<String, List<String>> existingLocalNamesMap = getExistingLocalNamesMapByLocalesAndCityNames(locales, cityNames);
        removeAlreadyExistingLocalNamesDtoFromList(createDtos, existingLocalNamesMap);

        return createDtos;
    }

    private Map<String, List<String>> getExistingLocalNamesMapByLocalesAndCityNames(
            List<String> locales, List<String> cityNames) {

        Map<String, List<String>> existingLocalNamesMap = new HashMap<>();
        for (String locale : locales) {
            List<String> names = cityLocaleNameRepository.findCityNamesByLocaleAndCityNames(locale, cityNames);
            existingLocalNamesMap.put(locale, names);
        }
        return existingLocalNamesMap;
    }

    private void removeAlreadyExistingLocalNamesDtoFromList(
            List<CityLocaleNameCreateDto> createDtos,
            Map<String, List<String>> existingLocalNamesMap) {

        createDtos.removeIf(dto -> {
            List<String> cities = existingLocalNamesMap.get(dto.getLocale());
            return cities != null && cities.contains(dto.getCityName());
        });
    }
}
