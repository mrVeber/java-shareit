package ru.practicum.shareit.exception.model;

public class NotAvailableToBookOwnItemsException extends RuntimeException {
    public NotAvailableToBookOwnItemsException(String message) {
        super(message);
    }
}