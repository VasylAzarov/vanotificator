package com.example.vanotificator.dto;

import lombok.Data;

@Data
public class UserDataRequestDto {
    private Long chatId;

    private double lat;

    private double lon;
}
