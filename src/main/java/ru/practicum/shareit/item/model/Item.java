package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Item {
    private long itemId;
    private long ownerId;
    private String name;
    private String description;
    private boolean isAvailable;
}
