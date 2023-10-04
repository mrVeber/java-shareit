package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoCreate {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private long itemId;
}
