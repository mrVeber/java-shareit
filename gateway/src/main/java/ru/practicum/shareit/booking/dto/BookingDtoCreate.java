package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.validators.Create;
import ru.practicum.shareit.validators.Update;
import ru.practicum.shareit.booking.validation.EndAfterStartValidation;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EndAfterStartValidation(groups = {Create.class, Update.class})
public class BookingDtoCreate {
    private long id;
    @FutureOrPresent(groups = Create.class)
    private LocalDateTime start;
    private LocalDateTime end;
    private long itemId;
    private long bookerId;
    private BookingStatus status;
}