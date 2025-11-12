package com.example.vanotificator.dto;

import lombok.Data;

@Data
public class TelegramUserDto {
    private Long chatId;

    private String username;

    private String cityName;
}
