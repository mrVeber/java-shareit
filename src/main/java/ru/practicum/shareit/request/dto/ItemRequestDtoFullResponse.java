package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDtoFullResponse {
    private long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDtoResponse> items;
}
