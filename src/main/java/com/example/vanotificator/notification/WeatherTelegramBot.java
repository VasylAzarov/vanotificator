package com.example.vanotificator.notification;

import com.example.vanotificator.dto.TelegramCreateDto;
import com.example.vanotificator.dto.UserDataRequestDto;
import com.example.vanotificator.service.CityService;
import com.example.vanotificator.service.TelegramUserService;
import com.example.vanotificator.service.WeatherNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
public class WeatherTelegramBot implements LongPollingSingleThreadUpdateConsumer {

    private static final Logger log = LoggerFactory.getLogger(WeatherTelegramBot.class);

    private final TelegramClient telegramClient;
    private final TelegramUserService telegramUserService;
    private final WeatherNotificationService weatherNotificationService;
    private final CityService cityService;

    public WeatherTelegramBot(@Value("${telegram.bot.token}") String botToken,
                              TelegramUserService telegramUserService,
                              WeatherNotificationService weatherNotificationService, CityService cityService) {
        this.telegramClient = new OkHttpTelegramClient(botToken);
        this.telegramUserService = telegramUserService;
        this.weatherNotificationService = weatherNotificationService;
        this.cityService = cityService;
    }

    @Override
    public void consume(Update update) {
        try {
            if (update.hasMessage()) {
                if (update.getMessage().hasText()) {
                    handleText(update);
                } else if (update.getMessage().hasLocation()) {
                    handleLocation(update);
                }
            } else if (update.hasInlineQuery()) {
                handleInlineQuery(update.getInlineQuery());
            }
        } catch (Exception e) {
            log.error("⚠️ Error processing update: {}", e.getMessage());
        }
    }

    private void handleText(Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        if (text.equalsIgnoreCase("/start")) {
            if (!telegramUserService.isUserExist(chatId)) {
                String username = update.getMessage().getFrom().getUserName();
                TelegramCreateDto user = new TelegramCreateDto();
                user.setChatId(chatId);
                user.setUsername(username);
                telegramUserService.createTelegramUser(user);
            }
            sendWelcome(chatId);
        } else if (text.equalsIgnoreCase("Choose city")) {
            sendCityInstruction(chatId);
        } else if (text.startsWith("/forecast:")) {
            handleCityText(chatId, text);
        } else {
            sendText(chatId, "Unknown command");
        }
    }

    private void handleLocation(Update update) {
        Long chatId = update.getMessage().getChatId();
        UserDataRequestDto dto = new UserDataRequestDto();
        dto.setChatId(chatId);
        dto.setLat(update.getMessage().getLocation().getLatitude());
        dto.setLon(update.getMessage().getLocation().getLongitude());
        String forecast = weatherNotificationService.buildNearestWeatherMessage(dto, null);
        sendText(chatId, forecast);
    }

    private void handleInlineQuery(InlineQuery inlineQuery) {
        int inlineResultLimit = 25;
        String query = inlineQuery.getQuery();
        String normalized = query.trim();

        if (normalized.length() < 12 && !normalized.startsWith("/forecast:")) {
            answerInline(inlineQuery.getId(), List.of());
            return;
        }
        normalized = normalized.replace("/forecast:", "");
        List<String> cities = cityService.getCitiesNamesByNamePart(normalized);
        List<String> limited = cities.stream()
                .limit(inlineResultLimit)
                .toList();

        List<InlineQueryResultArticle> results = buildInlineResults(limited);
        answerInline(inlineQuery.getId(), results);
    }

    private List<InlineQueryResultArticle> buildInlineResults(List<String> cityNames) {
        return cityNames.stream()
                .map(name -> new InlineQueryResultArticle(
                        "city_" + name,
                        name,
                        new InputTextMessageContent("/forecast:" + name)
                ))
                .toList();
    }

    private void answerInline(String inlineQueryId, List<InlineQueryResultArticle> results) {
        AnswerInlineQuery answer = AnswerInlineQuery
                .builder()
                .inlineQueryId(inlineQueryId)
                .results(results)
                .build();
        answer.setCacheTime(0);
        answer.setIsPersonal(true);

        try {
            telegramClient.execute(answer);
        } catch (TelegramApiException e) {
            log.error("⚠️ Telegram API error: {}", e.getMessage());
        }
    }

    private void handleCityText(Long chatId, String text) {
        UserDataRequestDto dto = new UserDataRequestDto();
        dto.setChatId(chatId);

        String cityName = text.replace("/forecast:", "");
        String forecast = weatherNotificationService.buildNearestWeatherMessage(dto, cityName);
        sendText(chatId, forecast);
    }

    private void sendWelcome(Long chatId) {
        SendMessage message = new SendMessage(chatId.toString(),
                "👋 Welcome! Please share your location to get a weather forecast or choose city from the list");
        message.setReplyMarkup(buildMainMenuKeyboard());
        executeSend(message);
    }

    private ReplyKeyboardMarkup buildMainMenuKeyboard() {
        KeyboardButton locationBtn = new KeyboardButton("📍 Send location");
        locationBtn.setRequestLocation(true);
        KeyboardButton cityBtn = new KeyboardButton("Choose city");
        KeyboardRow row = new KeyboardRow();
        row.add(locationBtn);
        row.add(cityBtn);
        return new ReplyKeyboardMarkup(List.of(row), true, false, false, null, false);
    }

    private void sendText(Long chatId, String text) {
        SendMessage message = new SendMessage(chatId.toString(), text);
        executeSend(message);
    }

    private void executeSend(SendMessage message) {
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            log.error("⚠️ Telegram API error for chat {}: {}",
                    message.getChatId(),
                    e.getMessage());
        }
    }

    private void sendCityInstruction(Long chatId) {
        InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text("🔍 Search city")
                .switchInlineQueryCurrentChat(" /forecast:")
                .build();

        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(button);

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboardRow(row)
                .build();

        SendMessage msg = SendMessage.builder()
                .chatId(chatId.toString())
                .text("✍️ Please, press the button below and start typing city name")
                .replyMarkup(markup)
                .build();
        executeSend(msg);
    }
}
