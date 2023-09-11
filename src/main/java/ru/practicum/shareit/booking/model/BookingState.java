package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.model.BookingException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState checkAndConvert(String source) {
        try {
            return BookingState.valueOf(source);
        } catch (Exception e) {
            throw new BookingException(String.format("Unknown state: %S", source));
        }
    }
}