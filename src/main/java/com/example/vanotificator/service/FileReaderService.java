package com.example.vanotificator.service;

import com.example.vanotificator.dto.CityLocaleNameFromFileDto;
import com.example.vanotificator.exeption.FileReadingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.example.vanotificator.dto.CityCreateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class FileReaderService {

    public List<CityCreateDto> readCityFromFile() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream is = new ClassPathResource("city.data/cities.json").getInputStream();
            return objectMapper.readValue(is, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new FileReadingException(
                    "Error while reading cities from file: \n"
                            + e.getMessage());
        }
    }

    public List<CityLocaleNameFromFileDto> readCityLocaleFromFile() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream is = new ClassPathResource("city.data/cities-locales.json").getInputStream();
            return objectMapper.readValue(is, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new FileReadingException(
                    "Error while reading cities locales from file: \n"
                            + e.getMessage());
        }
    }
}
