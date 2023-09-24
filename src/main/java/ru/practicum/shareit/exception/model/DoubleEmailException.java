package ru.practicum.shareit.exception.model;

import org.springframework.dao.DataIntegrityViolationException;

public class DoubleEmailException extends DataIntegrityViolationException {

    public DoubleEmailException(String message) {
        super(message);
    }
}
