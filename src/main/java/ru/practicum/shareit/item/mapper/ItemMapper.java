package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CreateUpdateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface ItemMapper {
    ItemDto toItemDto(Item item);

    Item toItem(ItemDto itemDto);

    Item toItem(CreateUpdateItemDto itemDto);
}