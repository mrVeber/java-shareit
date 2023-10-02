package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ItemRequestDtoRS {
    private Long id;
    private Long requesterId;
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}
