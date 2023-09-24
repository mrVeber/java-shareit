package ru.practicum.shareit.exception.model;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class DoubleEmailException extends DataIntegrityViolationException {

    public DoubleEmailException(String message) {
        super(message);
    }
}
