package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemDtoOut getItemById(long itemId, long userId);

    List<ItemDtoOut> getItemsByOwner(Integer from, Integer size, long userId);

    List<ItemDtoOut> getItemBySearch(Integer from, Integer size, String text);

    ItemDtoOut saveNewItem(ItemDtoIn itemDtoIn, long userId);

    ItemDtoOut updateItem(long itemId, ItemDtoIn itemDtoIn, long userId);

    CommentDtoOut saveNewComment(long itemId, CommentDtoIn commentDtoIn, long userId);

}