package ru.practicum.shareit.exception.model;

public class BookingCanBeApprovedOnlyByOwnerException extends RuntimeException {
    public BookingCanBeApprovedOnlyByOwnerException(String message) {
        super(message);
    }
}