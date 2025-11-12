package com.example.vanotificator.dto;

import lombok.Data;

@Data
public class TelegramUserUpdateDto {
    private Long chatId;

    private String cityName;
}
