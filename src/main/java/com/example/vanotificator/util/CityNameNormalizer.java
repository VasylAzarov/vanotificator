package com.example.vanotificator.util;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CityNameNormalizer {
    private static final Map<String, String> ALIASES = Map.of(
            "Donji Grad", "Zagreb",
            "Odesa", "Odessa",
            "Kiev", "Kyiv",
            "Nur-Sultan", "Astana",
            "Rīga", "Riga",
            "Luxembourg province", "Luxembourg"
    );

    public String normalize(String raw) {
        String s = raw.toLowerCase();
        return ALIASES.getOrDefault(s, s);
    }
}