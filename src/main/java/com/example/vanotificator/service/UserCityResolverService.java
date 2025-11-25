package com.example.vanotificator.service;

import com.example.vanotificator.dto.*;
import com.example.vanotificator.exeption.CityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserCityResolverService {

    private final TelegramUserService telegramUserService;
    private final CityService cityService;
    private final RequestService requestService;

    public UserCityResolverService(
            TelegramUserService telegramUserService,
            CityService cityService,
            RequestService requestService
    ) {
        this.telegramUserService = telegramUserService;
        this.cityService = cityService;
        this.requestService = requestService;
    }

    public String resolveCityName(UserDataRequestDto dto) {
        String cityName;
        try {
            cityName = cityService.getCityByCoordinates(dto.getLat(), dto.getLon()).getName();
        } catch (CityNotFoundException e) {
            String detectedName = requestService.getGeoData(dto.getLat(), dto.getLon()).getCityName();
            try {
                cityName = cityService.getCityByName(detectedName).getName();
                cityName = cityService.updateCityCoordinates(cityName, dto.getLat(), dto.getLon()).getName();
            } catch (CityNotFoundException ignored) {
                CityCreateDto create = new CityCreateDto();
                create.setName(detectedName);
                create.setLat(dto.getLat());
                create.setLon(dto.getLon());
                cityName = cityService.createCity(create).getName();
            }
        }

        setCityIfNotExist(dto.getChatId(), cityName);
        return cityName;
    }

    private void setCityIfNotExist(long chatId, String cityName) {
        TelegramUserDto user = telegramUserService.getTelegramUserByChatId(chatId);
        if (user.getCityName() == null) {
            TelegramUserUpdateDto update = new TelegramUserUpdateDto();
            update.setChatId(chatId);
            update.setCityName(cityName);
            telegramUserService.updateUserCity(update);
        }
    }
}
