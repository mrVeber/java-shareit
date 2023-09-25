package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemDtoResponse create(ItemDtoRequest itemDto, long userId);

    ItemDtoFullResponse getById(long id, long ownerId);

    List<ItemDtoFullResponse> getItemsOneOwner(long userId, int from, int size);

    ItemDtoResponse update(long id, ItemDtoRequest itemDto, long userId);

    void delete(long id, long userId);

    List<ItemDtoResponse> search(String text, int from, int size);

    CommentDtoResponse createComment(long itemId, CommentDtoRequest commentDto, long userId);
}