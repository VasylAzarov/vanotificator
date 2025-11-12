package com.example.vanotificator.exeption;

public class ForecastNotFoundException extends RuntimeException {
    public ForecastNotFoundException(String message) {
        super(message);
    }
}
