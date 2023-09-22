package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortForRequest;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;

@UtilityClass
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        Long requestId = null;
        if (item.getRequest() != null) {
            requestId = item.getRequest().getId();
        }
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                requestId);

    }

    public Item toItem(ItemDto itemDto) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null,
                null);
    }


    public ItemWithBookingAndCommentsDto toItemWithBookingAndCommentsDto(Item item) {
        return new ItemWithBookingAndCommentsDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                new ArrayList<>());

    }

    public ItemShortForRequest toItemShortForRequest(Item item) {
        return new ItemShortForRequest(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequest().getId());
    }
}