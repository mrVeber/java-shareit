package ru.practicum.shareit.exception.model;

public class WrongNumbersForPagingException extends RuntimeException {
    public WrongNumbersForPagingException(String message) {
        super(message);
    }
}
