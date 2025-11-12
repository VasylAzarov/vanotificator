package com.example.vanotificator.util;

import org.springframework.stereotype.Service;

@Service
public class WeatherUtil {

    public Probability getPrecipitationProbability(double pop) {
        if (pop == 0.0) {
            return Probability.NO;
        }
        if (pop < 0.4) {
            return Probability.LOW;
        }
        if (pop < 0.7) {
            return Probability.MIDDLE;
        }
        return Probability.HIGH;
    }

    public Lvl getWindLvl(double windSpeed) {
        if (windSpeed <= 1.5) {
            return Lvl.CALM;
        }
        if (windSpeed <= 5) {
            return Lvl.LIGHT;
        }
        if (windSpeed <= 10) {
            return Lvl.MIDDLE;
        }
        return Lvl.STRONG;
    }

    public Lvl getPrecipitationLvl(double precipitation) {
        if (precipitation <= 0.1) {
            return Lvl.CALM;
        }
        if (precipitation <= 2) {
            return Lvl.LIGHT;
        }
        if (precipitation <= 5) {
            return Lvl.MIDDLE;
        }
        return Lvl.STRONG;
    }

    public Temperature getTemperatureLvl(double temperature) {
        if (temperature <= -21) {
            return Temperature.EXTREMELY_COLD;
        }
        if (temperature <= -11) {
            return Temperature.VERY_COLD;
        }
        if (temperature <= -6) {
            return Temperature.COLD;
        }
        if (temperature <= -1) {
            return Temperature.FREEZING_COLD;
        }
        if (temperature <= 4) {
            return Temperature.CHILLY;
        }
        if (temperature <= 9) {
            return Temperature.COOL;
        }
        if (temperature <= 14) {
            return Temperature.MILD;
        }
        if (temperature <= 19) {
            return Temperature.MILD_WARM;
        }
        if (temperature <= 24) {
            return Temperature.WARM;
        }
        if (temperature <= 29) {
            return Temperature.WARM_STRONG;
        }
        return Temperature.HOT;
    }

    public CloudCoveredge getCloudCoveredge(int clouds) {
        if (clouds <= 25) {
            return CloudCoveredge.CLEAR;
        }
        if (clouds < 50) {
            return CloudCoveredge.PARTLY_CLOUDY;
        }
        if (clouds < 75) {
            return CloudCoveredge.CLOUDY;
        }
        return CloudCoveredge.OVERCAST;
    }

    public enum Lvl {
        CALM, LIGHT, MIDDLE, STRONG
    }

    public enum Probability {
        NO, LOW, MIDDLE, HIGH
    }

    public enum Temperature {
        EXTREMELY_COLD, VERY_COLD, COLD,
        FREEZING_COLD, CHILLY, COOL, MILD,
        MILD_WARM, WARM, WARM_STRONG, HOT
    }

    public enum CloudCoveredge {
        CLEAR, PARTLY_CLOUDY, CLOUDY, OVERCAST
    }
}
