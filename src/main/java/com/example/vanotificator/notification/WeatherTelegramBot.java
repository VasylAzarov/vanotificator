package com.example.vanotificator.notification;

import com.example.vanotificator.dto.*;
import com.example.vanotificator.service.TelegramUserService;
import com.example.vanotificator.service.WeatherNotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
public class WeatherTelegramBot implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;
    private final TelegramUserService telegramUserService;
    private final WeatherNotificationService weatherNotificationService;

    public WeatherTelegramBot(
            @Value("${telegram.bot.token}") String botToken,
            TelegramUserService telegramUserService,
            WeatherNotificationService weatherNotificationService) {
        this.telegramClient = new OkHttpTelegramClient(botToken);
        this.telegramUserService = telegramUserService;
        this.weatherNotificationService = weatherNotificationService;
    }

    @Override
    public void consume(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                handleTextMessage(update);
            } else if (update.hasMessage() && update.getMessage().hasLocation()) {
                handleLocation(update);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error processing update: " + e.getMessage());
        }
    }

    private void handleTextMessage(Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        String username = update.getMessage().getFrom().getUserName();

        if (text.equalsIgnoreCase("/start")) {
            if (!telegramUserService.isUserExist(chatId)) {
                TelegramCreateDto telegramCreateDto = new TelegramCreateDto();
                telegramCreateDto.setChatId(chatId);
                telegramCreateDto.setUsername(username);
                telegramUserService.createTelegramUser(telegramCreateDto);
            }
            sendWelcomeMessage(chatId);
        }
    }

    private void handleLocation(Update update) {
        Long chatId = update.getMessage().getChatId();
        double lat = update.getMessage().getLocation().getLatitude();
        double lon = update.getMessage().getLocation().getLongitude();

        UserDataRequestDto dto = new UserDataRequestDto();
        dto.setChatId(chatId);
        dto.setLat(lat);
        dto.setLon(lon);
        String response = weatherNotificationService.buildNearestWeatherMessage(dto);
        sendMessage(chatId, response);
    }

    private void sendWelcomeMessage(Long chatId) {
        SendMessage message = new SendMessage(chatId.toString(),
                "👋 Welcome! Please share your location to get a weather forecast.");

        KeyboardButton locationButton = new KeyboardButton("📍 Send location(phone only)");
        locationButton.setRequestLocation(true);

        KeyboardRow row = new KeyboardRow(List.of(locationButton));
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup(List.of(row));
        keyboard.setResizeKeyboard(true);

        message.setReplyMarkup(keyboard);
        executeSafe(message);
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage(chatId.toString(), text);
        executeSafe(message);
    }

    private void executeSafe(SendMessage message) {
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            System.err.println("⚠️ Telegram API error for chat " + message.getChatId() + ": " + e.getMessage());
        }
    }
}
