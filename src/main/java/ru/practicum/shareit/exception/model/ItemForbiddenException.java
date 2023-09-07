package ru.practicum.shareit.exception.model;

public class ItemForbiddenException extends RuntimeException {
    public ItemForbiddenException(String message) {
        super(message);
    }
}
