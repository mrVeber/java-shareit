package ru.practicum.shareit.exception.model;

public class EmailExeption extends RuntimeException {
    public EmailExeption(String message) {
        super(message);
    }
}
