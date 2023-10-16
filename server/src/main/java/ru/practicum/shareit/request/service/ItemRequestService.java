package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoRQ;
import ru.practicum.shareit.request.dto.ItemRequestDtoRS;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoRS create(long userId, ItemRequestDtoRQ itemRequestDtoRQ);

    List<ItemRequestDtoRS> getInfo(long userId);

    ItemRequestDtoRS getInfo(long userId, long requestId);

    List<ItemRequestDtoRS> getRequests(long userId, int from, int size);
}
