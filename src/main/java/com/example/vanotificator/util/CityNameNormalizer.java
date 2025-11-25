package com.example.vanotificator.util;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CityNameNormalizer {
    private static final Map<String, String> ALIASES = Map.of(
            "donji grad", "zagreb",
            "odesa", "odessa",
            "kiev", "kyiv",
            "nur-sultan", "astana",
            "rīga", "riga",
            "luxembourg province", "luxembourg"
    );

    public String normalize(String raw) {
        String s = raw.toLowerCase();
        return ALIASES.getOrDefault(s, s);
    }
}