package com.example.vanotificator.mapper;

import com.example.vanotificator.dto.ForecastDto;
import com.example.vanotificator.dto.WeatherResponseDto;
import com.example.vanotificator.dto.forecast.response.parts.WeatherListItem;
import com.example.vanotificator.model.Forecast;
import com.example.vanotificator.util.DateTimeUtil;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ForecastMapper {

    default List<Forecast> toEntity(WeatherResponseDto weatherResponseDto) {
        List<Forecast> list = new ArrayList<>();
        for (WeatherListItem item : weatherResponseDto.getList()) {

            Forecast forecast = new Forecast();


            DateTimeFormatter api_formater =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime =
                    LocalDateTime.parse(item.getDtTxt(), api_formater);

            forecast.setDate(dateTime.toLocalDate());
            forecast.setTime(dateTime.toLocalTime());
            forecast.setDayOfWeek(dateTime.getDayOfWeek());

            forecast.setTemperature(item.getMain().getTemp());
            forecast.setPop(item.getPop());
            forecast.setPrecipitation(item.getRain().getThreeH());
            forecast.setWindSpeed(item.getWind().getSpeed());
            forecast.setClouds(item.getClouds().getAll());

            list.add(forecast);
        }
        return list;
    }

    default ForecastDto toDto(Forecast forecast) {
        ForecastDto forecastDto = new ForecastDto();

        ZonedDateTime zonedDateTime =
                DateTimeUtil.convertTimeZone(forecast.getDate(),
                        forecast.getTime(),
                        forecast.getCity().getTimezone());

        forecastDto.setDate(zonedDateTime.toLocalDate());
        forecastDto.setTime(zonedDateTime.toLocalTime());
        forecastDto.setDayOfWeek(zonedDateTime.getDayOfWeek());
        forecastDto.setTemperature(forecast.getTemperature());
        forecastDto.setPrecipitation(forecast.getPrecipitation());
        forecastDto.setPop(forecast.getPop());
        forecastDto.setWindSpeed(forecast.getWindSpeed());
        forecastDto.setClouds(forecast.getClouds());
        forecastDto.setCityName(forecast.getCity().getName());

        return forecastDto;
    }

    List<ForecastDto> toDto(List<Forecast> forecasts);
}
