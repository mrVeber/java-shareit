package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.validators.EndAfterStartValidation;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EndAfterStartValidation
public class NewBooking {
    @NotNull
    private Long itemId;
    @FutureOrPresent
    private LocalDateTime start;
    private LocalDateTime end;
}
