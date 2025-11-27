package com.example.vanotificator.exeption;

public class FileReadingException extends RuntimeException {
    public FileReadingException(String message) {
        super(message);
    }
}
