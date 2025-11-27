package com.example.vanotificator.mapper;

import com.example.vanotificator.dto.CityLocaleNameCreateDto;
import com.example.vanotificator.dto.CityLocaleNameFromFileDto;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface CityLocaleMapper {

    default List<CityLocaleNameCreateDto> toCreateDto(List<CityLocaleNameFromFileDto> dtos) {
        List<CityLocaleNameCreateDto> result = new ArrayList<>();

        for (CityLocaleNameFromFileDto dto : dtos) {
            for (Map.Entry<String, String> entry : dto.getLocaleName().entrySet()) {
                CityLocaleNameCreateDto cityLocaleNameCreateDto = new CityLocaleNameCreateDto();
                cityLocaleNameCreateDto.setCityName(dto.getName());
                cityLocaleNameCreateDto.setLocale(entry.getKey());
                cityLocaleNameCreateDto.setName(entry.getValue());
                result.add(cityLocaleNameCreateDto);
            }
        }
        return result;
    }
}
