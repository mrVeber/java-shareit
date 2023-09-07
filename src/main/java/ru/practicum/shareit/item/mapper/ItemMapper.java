package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static StandardItemDto toStandardItemDto(Item item, List<CommentDto> comments) {
        StandardItemDto itemDto = new StandardItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setComments(comments);
        return itemDto;
    }

    public static WithBookingItemDto toWithBookingItemDto(Item item, List<CommentDto> comments, Optional<Booking> lastBooking, Optional<Booking> nextBooking) {
        return WithBookingItemDto
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking
                        .map(booking -> new ShortBookingDto(booking.getId(), booking.getBooker().getId()))
                        .orElse(null)
                )
                .nextBooking(nextBooking
                        .map(booking -> new ShortBookingDto(booking.getId(), booking.getBooker().getId()))
                        .orElse(null)
                )
                .comments(comments)
                .build();
    }

    public static Item fromStandardItemDto(StandardItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }
}

