package com.example.vanotificator.dto.forecast.response.parts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Rain {
    @JsonProperty("3h")
    private double threeH = 0.0;
}
