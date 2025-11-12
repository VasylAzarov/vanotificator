package com.example.vanotificator.dto;

import lombok.Data;
import java.util.List;

@Data
public class CityDto {
    private Long id;

    private String name;

    private List<CoordinatesDto> coordinates;
}
