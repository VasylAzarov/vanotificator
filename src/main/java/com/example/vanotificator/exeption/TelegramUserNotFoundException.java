package com.example.vanotificator.exeption;

public class TelegramUserNotFoundException extends RuntimeException {
    public TelegramUserNotFoundException(String message) {
        super(message);
    }
}
