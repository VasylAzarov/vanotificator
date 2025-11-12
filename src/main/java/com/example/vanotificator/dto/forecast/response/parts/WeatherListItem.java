package com.example.vanotificator.dto.forecast.response.parts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Setter(AccessLevel.NONE)
@Getter
public class WeatherListItem {
    private long dt;

    private Main main;

    private List<Weather> weather;

    private Clouds clouds;

    private Wind wind;

    private int visibility;

    private double pop;

    private Sys sys;

    @JsonProperty("dt_txt")
    private String dtTxt;

    private Rain rain = new Rain();
}
