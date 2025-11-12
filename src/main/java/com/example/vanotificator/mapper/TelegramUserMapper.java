package com.example.vanotificator.mapper;

import com.example.vanotificator.dto.TelegramUserDto;
import com.example.vanotificator.model.TelegramUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TelegramUserMapper {

    @Mapping(target = "cityName", source = "city.name")
    TelegramUserDto toDto(TelegramUser telegramUser);
}
