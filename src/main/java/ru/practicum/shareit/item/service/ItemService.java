package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto update(ItemDto itemDto, long itemId, Long userIdInHeader);

    List<ItemWithBookingAndCommentsDto> getAllByUserId(long ownerId, int from, int size);

    ItemWithBookingAndCommentsDto getById(long id, long userId);

    List<ItemDto> search(String text, int from, int size);

    CommentDtoOutput createComment(CommentDtoInput commentDto, Long userId, Long itemId);



}