package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestInputDto;
import ru.practicum.shareit.request.dto.RequestOutputDto;

import java.util.List;

public interface ItemRequestService {

    RequestOutputDto createRequest(RequestInputDto requestInputDto, long userId);

    List<RequestOutputDto> getRequestsByAuthor(long userId);

    List<RequestOutputDto> getAllRequests(Long userId, Integer from, Integer size);

    RequestOutputDto getRequestById(long requestId, long userId);


}
