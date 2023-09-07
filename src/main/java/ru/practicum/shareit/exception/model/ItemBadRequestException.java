package ru.practicum.shareit.exception.model;

public class ItemBadRequestException extends RuntimeException {
    public ItemBadRequestException(String message) {
        super(message);
    }
}
