package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.validators.EndAfterStartValidation;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@EndAfterStartValidation
public class BookingDtoIn {

    @FutureOrPresent
    private LocalDateTime start;

    private LocalDateTime end;

    @NotNull
    private Long itemId;
}
