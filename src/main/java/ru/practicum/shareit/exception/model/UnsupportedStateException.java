package ru.practicum.shareit.exception.model;

public class UnsupportedStateException extends RuntimeException {
    public UnsupportedStateException(String message) {
        super(message);
    }
}
