package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import ru.practicum.shareit.validators.EndAfterStartValidation;
import javax.validation.constraints.Future;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
@EndAfterStartValidation
public class BookingDtoRequest {
    @Positive
    private long itemId;

    @Future
    private LocalDateTime start;

    private LocalDateTime end;
}
