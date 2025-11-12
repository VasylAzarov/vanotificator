package com.example.vanotificator.service;

import com.example.vanotificator.dto.GeoResponseDto;
import com.example.vanotificator.dto.WeatherResponseDto;
import com.example.vanotificator.exeption.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class RequestService {

    private final RestTemplate restTemplate;
    private final String key;
    private final String baseUrl;
    private final String dataPath;
    private final String geoPath;
    private final String weatherApiVersion;
    private final String geoApiVersion;

    @Autowired
    public RequestService(
            RestTemplate restTemplate,
            @Value("${owm.api-key}") String key,
            @Value("${owm.base-url}") String baseUrl,
            @Value("${owm.data-path}") String dataPath,
            @Value("${owm.geo-path}") String geoPath,
            @Value("${owm.weather-api-version}") String weatherApiVersion,
            @Value("${owm.geo-api-version}") String geoApiVersion
    ) {
        this.restTemplate = restTemplate;
        this.key = key;
        this.baseUrl = baseUrl;
        this.dataPath = dataPath;
        this.geoPath = geoPath;
        this.weatherApiVersion = weatherApiVersion;
        this.geoApiVersion = geoApiVersion;
    }

    public WeatherResponseDto getWeather(String cityName) {
        String path = dataPath + weatherApiVersion + "/forecast";
        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path(path)
                .queryParam("q", cityName)
                .queryParam("appid", key)
                .queryParam("units", "metric")
                .toUriString();

        return executeGet(url, WeatherResponseDto.class);
    }

    public GeoResponseDto getGeoData(double lat, double lon) {
        String path = geoPath + geoApiVersion + "/reverse";
        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path(path)
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("appid", key)
                .toUriString();

        return executeGet(url, GeoResponseDto[].class)[0];
    }

    private <T> T executeGet(String url, Class<T> responseType) {
        try {
            ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    responseType
            );

            int status = response.getStatusCode().value();

            if (status >= 200 && status < 300) {
                return response.getBody();
            } else if (status >= 300 && status < 400) {
                throw new ApiRequestException("Redirection error: " + status);
            } else if (status >= 400 && status < 500) {
                throw new ApiRequestException("Client error: " + status);
            } else if (status >= 500) {
                throw new ApiRequestException("Server error: " + status);
            } else {
                throw new ApiRequestException("Unexpected HTTP status: " + status);
            }

        } catch (HttpClientErrorException e) {
            throw new ApiRequestException("Client error: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            throw new ApiRequestException("Server error: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        }
    }
}
