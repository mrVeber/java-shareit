package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.model.ExceptionForUnsupport;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState getState(String text) {
        if ((text == null) || text.isBlank()) {
            return BookingState.ALL;
        }
        try {
            return BookingState.valueOf(text.toUpperCase().trim());
        } catch (Exception e) {
            throw new ExceptionForUnsupport("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
