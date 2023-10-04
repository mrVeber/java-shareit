package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.validation.EndAfterStartValidation;
import ru.practicum.shareit.validators.Create;
import ru.practicum.shareit.validators.Update;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EndAfterStartValidation(groups = {Create.class, Update.class})
public class BookingDtoRequest {
    private long id;

    @FutureOrPresent(groups = {Create.class})
    private LocalDateTime start;

    private LocalDateTime end;

    @NotNull(groups = {Create.class})
    private Long itemId;
}
