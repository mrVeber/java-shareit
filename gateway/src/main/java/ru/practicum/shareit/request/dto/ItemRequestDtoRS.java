package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestDtoRS {
    private Long id;
    private Long requesterId;
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}
