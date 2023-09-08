package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.validators.Create;
import ru.practicum.shareit.validators.EndAfterStartValidation;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EndAfterStartValidation
public class InputBookingDto {
    @NotNull(groups = {Create.class})
    private Long itemId;
    //@NotNull(message = "start = null")
    @FutureOrPresent(message = "start mustn't be future")
    private LocalDateTime start;
    //@NotNull(message = "end = null")
    @Future(message = "end mustn't be past")
    private LocalDateTime end;
}
