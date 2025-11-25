package com.example.vanotificator.mapper;

import com.example.vanotificator.dto.CityCreateDto;
import com.example.vanotificator.dto.CityDto;
import com.example.vanotificator.dto.CoordinatesDto;
import com.example.vanotificator.model.City;
import com.example.vanotificator.model.Coordinates;
import com.example.vanotificator.util.CityUtil;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CityMapper {

    CityDto toDto(City city);

    CoordinatesDto toDto(Coordinates coordinates);

    default City fromCreateDto(CityCreateDto createDto) {
        City city = new City();

        double lat = CityUtil.roundCoordinate(createDto.getLat());
        double lon = CityUtil.roundCoordinate(createDto.getLon());

        Coordinates coordinates = new Coordinates();
        coordinates.setLat(lat);
        coordinates.setLon(lon);
        coordinates.setCity(city);

        city.setName(createDto.getName().toLowerCase());
        city.getCoordinates().add(coordinates);
        city.setTimezone(createDto.getTimezone());
        return city;
    }



}
