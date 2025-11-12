package com.example.vanotificator.mapper;

import com.example.vanotificator.dto.CityDto;
import com.example.vanotificator.dto.CoordinatesDto;
import com.example.vanotificator.model.City;
import com.example.vanotificator.model.Coordinates;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CityMapper {

    CityDto toDto(City city);

    CoordinatesDto toDto(Coordinates coordinates);
}
