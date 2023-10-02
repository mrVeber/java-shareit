package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.validators.*;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EndAfterStartValidation(groups = {Create.class, Update.class})
public class BookingDtoCreate {
    private long id;
    @FutureOrPresent(groups = Create.class)
    private LocalDateTime start;
    @Future(groups = Create.class)
    private LocalDateTime end;
    private long itemId;
}
