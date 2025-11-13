package com.example.vanotificator.service;

import com.example.vanotificator.dto.CityCreateDto;
import com.example.vanotificator.dto.CityDto;
import com.example.vanotificator.dto.TelegramUserUpdateDto;
import com.example.vanotificator.dto.UserDataRequestDto;
import com.example.vanotificator.exeption.CityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserCityResolverService {

    private final TelegramUserService telegramUserService;
    private final CityService cityService;
    private final RequestService requestService;

    public UserCityResolverService(TelegramUserService telegramUserService,
                                   CityService cityService,
                                   RequestService requestService) {
        this.telegramUserService = telegramUserService;
        this.cityService = cityService;
        this.requestService = requestService;
    }

    public String resolveCityName(UserDataRequestDto dto) {
        String cityName;
        try {
            cityName = cityService.getCityByCoordinates(dto.getLat(), dto.getLon()).getName();
        } catch (CityNotFoundException e) {
            String name = requestService.getGeoData(dto.getLat(), dto.getLon()).getCityName();

            CityDto city = cityService.getCityByName(name);

            if(city == null) {
                CityCreateDto cityCreateDto = new CityCreateDto();
                cityCreateDto.setName(name);
                cityCreateDto.setLat(dto.getLat());
                cityCreateDto.setLon(dto.getLon());
                cityName = cityService.createCity(cityCreateDto).getName();
            } else {
                cityName = cityService.updateCityCoordinates(
                        city.getName(),
                        dto.getLat(),
                        dto.getLon()).getName();
            }

        }
        setCityIfNotExist(dto.getChatId(), cityName);
        return cityName;
    }

    private void setCityIfNotExist(long chatId, String cityName) {
        if (telegramUserService.getTelegramUserByChatId(chatId).getCityName() == null) {
            TelegramUserUpdateDto telegramUserUpdateDto = new TelegramUserUpdateDto();
            telegramUserUpdateDto.setChatId(chatId);
            telegramUserUpdateDto.setCityName(cityName);
            telegramUserService.updateUserCity(telegramUserUpdateDto);
        }
    }
}
