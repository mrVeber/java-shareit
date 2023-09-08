package ru.practicum.shareit.validators;

import ru.practicum.shareit.booking.dto.InputBookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EndAfterStartValidator implements ConstraintValidator<EndAfterStartValidation, InputBookingDto> {
    @Override
    public boolean isValid(InputBookingDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        return bookingDto.getStart() != null &&
                bookingDto.getEnd() != null &&
                bookingDto.getStart().isBefore(bookingDto.getEnd());
    }
}
