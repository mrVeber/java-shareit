package ru.practicum.shareit.exception.model;

public class NotBookerException extends RuntimeException {
    public NotBookerException(String message) {
        super(message);
    }
}
