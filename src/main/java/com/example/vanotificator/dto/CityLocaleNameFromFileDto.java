package com.example.vanotificator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class CityLocaleNameFromFileDto {

private String name;

@JsonProperty("locale-name")
private Map<String, String> localeName;

}
