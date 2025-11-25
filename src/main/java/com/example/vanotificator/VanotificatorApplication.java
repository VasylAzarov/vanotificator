package com.example.vanotificator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class VanotificatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(VanotificatorApplication.class, args);
    }
}
