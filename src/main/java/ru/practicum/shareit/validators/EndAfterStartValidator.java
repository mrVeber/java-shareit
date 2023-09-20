package ru.practicum.shareit.validators;

import ru.practicum.shareit.booking.dto.BookingDtoIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EndAfterStartValidator implements ConstraintValidator<EndAfterStartValidation, BookingDtoIn> {
    @Override
    public void initialize(EndAfterStartValidation constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingDtoIn newBooking,
                           ConstraintValidatorContext constraintValidatorContext) {
        return newBooking.getStart() != null &&
                newBooking.getEnd() != null &&
                newBooking.getStart().isBefore(newBooking.getEnd());
    }
}