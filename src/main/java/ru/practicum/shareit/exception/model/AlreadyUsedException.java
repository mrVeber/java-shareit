package ru.practicum.shareit.exception.model;

public class AlreadyUsedException extends RuntimeException {
    public AlreadyUsedException(String message) {
        super(message);
    }
}