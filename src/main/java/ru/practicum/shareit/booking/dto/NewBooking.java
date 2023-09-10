package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.validators.EndAfterStartValidation;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@EndAfterStartValidation
public class NewBooking {
    private Long itemId;
    @FutureOrPresent
    private LocalDateTime start;
    private LocalDateTime end;
}
