package com.example.vanotificator.dto;

import lombok.Data;

@Data
public class TelegramCreateDto {
    private Long chatId;

    private String username;
}
