package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ItemRequestDtoRQ {
    private Long id;
    private Long requesterId;
    private String description;
    private LocalDateTime created;
}