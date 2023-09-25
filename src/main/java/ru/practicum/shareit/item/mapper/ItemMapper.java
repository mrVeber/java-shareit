package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.dto.ItemDtoFullResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
@Slf4j
public class ItemMapper {
    public Item toItem(ItemDtoRequest itemDto, User owner) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .build();
    }

    public ItemDtoResponse toResponseItemDto(Item item) {
        return ItemDtoResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .requestId(item.getItemRequest() != null ? item.getItemRequest().getId() : 0)
                .build();
    }

    public ItemDtoFullResponse toFullResponseItemDto(Item item) {
        return ItemDtoFullResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .build();
    }

    public Item toUpdatedItem(Item item, ItemDtoRequest requestItemDto) {
        String itemDtoName = requestItemDto.getName();
        String itemDtoDescription = requestItemDto.getDescription();
        Boolean isAvailable = requestItemDto.getAvailable();

        if (itemDtoName != null && !itemDtoName.isEmpty()) {
            item.setName(requestItemDto.getName());
            log.info("Имя вещи изменено на {}.", itemDtoName);
        }
        if (itemDtoDescription != null && !itemDtoDescription.isEmpty()) {
            item.setDescription(requestItemDto.getDescription());
            log.info("Описание вещи изменено на {}.", itemDtoDescription);
        }
        if (isAvailable != null) {
            item.setAvailable(isAvailable);
            log.info("Статус бронирования вещи изменён на {}.", isAvailable);
        }
        return item;
    }

}