package com.example.vanotificator.service;

import com.example.vanotificator.dto.GeoResponseDto;
import com.example.vanotificator.dto.WeatherResponseDto;
import com.example.vanotificator.exeption.ApiRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class RequestService {

    private final RestTemplate restTemplate;
    private final String key1;
    private final String key2;
    private final String baseUrl;
    private final String dataPath;
    private final String geoPath;
    private final String weatherApiVersion;
    private final String geoApiVersion;

    public RequestService(
            RestTemplate restTemplate,
            @Value("${owm.api-key-1}") String key1,
            @Value("${owm.api-key-2}") String key2,
            @Value("${owm.base-url}") String baseUrl,
            @Value("${owm.data-path}") String dataPath,
            @Value("${owm.geo-path}") String geoPath,
            @Value("${owm.weather-api-version}") String weatherApiVersion,
            @Value("${owm.geo-api-version}") String geoApiVersion
    ) {
        this.restTemplate = restTemplate;
        this.key1 = key1;
        this.key2 = key2;
        this.baseUrl = baseUrl;
        this.dataPath = dataPath;
        this.geoPath = geoPath;
        this.weatherApiVersion = weatherApiVersion;
        this.geoApiVersion = geoApiVersion;
    }

    public WeatherResponseDto getWeather(String cityName) {
        try {
            return executeGet(buildWeatherUrl(cityName, key1), WeatherResponseDto.class);
        } catch (ApiRequestException e) {
            if (is429(e)) {
                return executeGet(buildWeatherUrl(cityName, key2), WeatherResponseDto.class);
            }
            throw e;
        }
    }

    public GeoResponseDto getGeoData(double lat, double lon) {
        try {
            GeoResponseDto[] arr = executeGet(buildGeoUrl(lat, lon, key1), GeoResponseDto[].class);
            return (arr != null && arr.length > 0) ? arr[0] : fallbackGeoNotFound();
        } catch (ApiRequestException e) {
            if (is429(e)) {
                GeoResponseDto[] arr = executeGet(buildGeoUrl(lat, lon, key2), GeoResponseDto[].class);
                return (arr != null && arr.length > 0) ? arr[0] : fallbackGeoNotFound();
            }
            throw e;
        }
    }

    private boolean is429(ApiRequestException e) {
        return e.getMessage() != null && e.getMessage().contains("429");
    }

    private GeoResponseDto fallbackGeoNotFound() {
        throw new ApiRequestException("Geo data not found for provided coordinates");
    }

    private String buildWeatherUrl(String cityName, String apiKey) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .path(dataPath + weatherApiVersion + "/forecast")
                .queryParam("q", cityName)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .toUriString();
    }

    private String buildGeoUrl(double lat, double lon, String apiKey) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .path(geoPath + geoApiVersion + "/reverse")
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("appid", apiKey)
                .toUriString();
    }

    private <T> T executeGet(String url, Class<T> responseType) {
        try {
            ResponseEntity<T> response = restTemplate.exchange(
                    URI.create(url), HttpMethod.GET, null, responseType
            );
            int status = response.getStatusCode().value();
            if (status >= 200 && status < 300) return response.getBody();
            if (status >= 300 && status < 400) throw new ApiRequestException("Redirection error: " + status);
            if (status == 429) throw new ApiRequestException("Client error: 429 Too Many Requests");
            if (status >= 400 && status < 500) throw new ApiRequestException("Client error: " + status);
            throw new ApiRequestException("Server error: " + status);
        } catch (HttpClientErrorException e) {
            throw new ApiRequestException("Client error: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            throw new ApiRequestException("Server error: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        }
    }
}

