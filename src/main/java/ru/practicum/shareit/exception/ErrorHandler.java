package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.model.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.error("Bad Request {}", e.getMessage());
        String errorMessage = "ValidationException: " + e.getMessage();
        return new ErrorResponse(errorMessage);
    }

    @ExceptionHandler
    @ResponseStatus (HttpStatus.BAD_REQUEST)
    public ErrorResponse validateArgumentException(final MethodArgumentNotValidException e) {
        log.error("Validation error: : {}", e.getMessage());
        String errorMessage = "Validation error: " + e.getMessage();
        return new ErrorResponse(errorMessage);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.error("NotFoundException: : {}", e.getMessage());
        String errorMessage = "NotFoundException: " + e.getMessage();
        return new ErrorResponse(errorMessage);
    }

    @ExceptionHandler
    @ResponseStatus (HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenException(final ForbiddenException e) {
        log.error("ForbiddenException: : {}", e.getMessage());
        String errorMessage = "ForbiddenException: " + e.getMessage();
        return new ErrorResponse(errorMessage);
    }

    @ExceptionHandler
    @ResponseStatus (HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleExceptionForUnsupport(final ExceptionForUnsupport e) {
        log.error("INTERNAL_SERVER_ERROR: : {}", e.getMessage());
        String strError = e.getMessage();
        return new ErrorResponse(strError);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.error("INTERNAL_SERVER_ERROR: : {}", e.getMessage());
        String strError = e.getMessage();
        return new ErrorResponse(strError);
    }

}
