package com.example.vanotificator.notification;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBotRegistrar {

    private static final Logger log = LoggerFactory.getLogger(TelegramBotRegistrar.class);

    private final WeatherTelegramBot weatherTelegramBot;
    private final String botToken;

    public TelegramBotRegistrar(WeatherTelegramBot weatherTelegramBot,
                                @Value("${telegram.bot.token}") String botToken) {
        this.weatherTelegramBot = weatherTelegramBot;
        this.botToken = botToken;
    }

    @PostConstruct
    public void startBot() {
        try {
            TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
            botsApplication.registerBot(botToken, weatherTelegramBot);
            log.info("✅ Telegram bot started successfully!");
        } catch (TelegramApiException e) {
            log.error("❌ Failed to start Telegram bot: {}", e.getMessage());
        }
    }
}
