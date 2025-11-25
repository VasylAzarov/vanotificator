package com.example.vanotificator.service;

import com.example.vanotificator.exeption.CityInitializationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.example.vanotificator.dto.CityCreateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class CityReaderService {

    public List<CityCreateDto> readCityFromFile() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream is = new ClassPathResource("city.data/cities.json").getInputStream();
            return objectMapper.readValue(is, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new CityInitializationException(
                    "Error while initialize cities from file: \n"
                            + e.getMessage());
        }
    }
}
