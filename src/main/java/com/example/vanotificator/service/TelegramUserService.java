package com.example.vanotificator.service;

import com.example.vanotificator.dto.TelegramCreateDto;
import com.example.vanotificator.dto.TelegramUserDto;
import com.example.vanotificator.dto.TelegramUserUpdateDto;
import com.example.vanotificator.exeption.TelegramUserNotFoundException;
import com.example.vanotificator.mapper.TelegramUserMapper;
import com.example.vanotificator.model.City;
import com.example.vanotificator.model.TelegramUser;
import com.example.vanotificator.repository.TelegramUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TelegramUserService {

    private final TelegramUserRepository telegramUserRepository;
    private final TelegramUserMapper telegramUserMapper;
    private final CityService cityService;

    public TelegramUserService(TelegramUserRepository telegramUserRepository,
                               TelegramUserMapper telegramUserMapper,
                               CityService cityService) {
        this.telegramUserRepository = telegramUserRepository;
        this.telegramUserMapper = telegramUserMapper;
        this.cityService = cityService;
    }

    public TelegramUserDto getTelegramUserByChatId(long chatId) {
        TelegramUser telegramUser = telegramUserRepository
                .findByChatId(chatId)
                .orElseThrow(() -> new TelegramUserNotFoundException(
                        "Telegram User with Chat ID [" + chatId + "] not found."
                ));
        return telegramUserMapper.toDto(telegramUser);
    }

    public void createTelegramUser(TelegramCreateDto telegramCreateDto) {
        TelegramUser telegramUser = new TelegramUser();
        telegramUser.setChatId(telegramCreateDto.getChatId());
        telegramUser.setUsername(telegramCreateDto.getUsername());
        telegramUserRepository.save(telegramUser);
    }

    public boolean isUserExist(long chatId) {
        return telegramUserRepository.existsByChatId(chatId);
    }

    @Transactional
    public void updateUserCity(TelegramUserUpdateDto telegramUserUpdateDto) {
        TelegramUser telegramUser = telegramUserRepository
                .findByChatId(telegramUserUpdateDto
                        .getChatId())
                .orElseThrow(() -> new TelegramUserNotFoundException(
                        "Telegram User with Chat ID ["
                                + telegramUserUpdateDto.getChatId()
                                + "] not found."
                ));
        City city = cityService
                .getCityEntityByName(telegramUserUpdateDto
                        .getCityName());
        telegramUser.setCity(city);
        telegramUserRepository.save(telegramUser);
    }
}
