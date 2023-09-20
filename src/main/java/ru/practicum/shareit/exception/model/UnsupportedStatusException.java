package ru.practicum.shareit.exception.model;

public class UnsupportedStatusException extends RuntimeException {
    public UnsupportedStatusException(String message) {
        super(message);
    }
}