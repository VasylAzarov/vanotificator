package com.example.vanotificator.dto.forecast.response.parts;

import lombok.Data;

@Data
public class City {

    private long id;

    private String name;

    private Coord coord;

    private String country;

    private int population;

    private int timezone;

    private long sunrise;

    private long sunset;
}
