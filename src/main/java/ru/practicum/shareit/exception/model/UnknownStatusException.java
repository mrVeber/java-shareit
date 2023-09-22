package ru.practicum.shareit.exception.model;

public class UnknownStatusException extends RuntimeException {

    public UnknownStatusException(String message) {
        super(message);
    }
}
