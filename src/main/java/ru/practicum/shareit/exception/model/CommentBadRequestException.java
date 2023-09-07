package ru.practicum.shareit.exception.model;

public class CommentBadRequestException extends RuntimeException {
    public CommentBadRequestException(String message) {
        super(message);
    }
}
