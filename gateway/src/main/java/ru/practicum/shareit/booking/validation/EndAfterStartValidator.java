package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.dto.BookingDtoCreate;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidator;
import java.time.LocalDateTime;

public class EndAfterStartValidator implements ConstraintValidator<EndAfterStartValidation, BookingDtoCreate> {

    @Override
    public void initialize(EndAfterStartValidation startBeforeEnd) {
    }

    @Override
    public boolean isValid(BookingDtoCreate bookingDto, ConstraintValidatorContext context) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }
}
