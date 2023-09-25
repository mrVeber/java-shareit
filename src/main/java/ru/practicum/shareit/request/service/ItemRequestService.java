package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.*;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDtoResponse create(ItemRequestDtoRequest request, long userId);

    ItemRequestDtoFullResponse getById(long id, long userId);

    List<ItemRequestDtoFullResponse> getOwnItemRequests(long userId);

    List<ItemRequestDtoFullResponse> getAll(long userId, int from, int size);
}
