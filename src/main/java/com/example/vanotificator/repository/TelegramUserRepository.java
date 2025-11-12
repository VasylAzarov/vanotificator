package com.example.vanotificator.repository;

import com.example.vanotificator.model.TelegramUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TelegramUserRepository extends JpaRepository<TelegramUser, Integer> {

    boolean existsByChatId(long chatId);

    @EntityGraph(attributePaths = {"city"})
    Optional<TelegramUser> findByChatId(long chatId);
}
