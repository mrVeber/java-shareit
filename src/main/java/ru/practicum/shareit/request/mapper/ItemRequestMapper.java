package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.RequestInputDto;
import ru.practicum.shareit.request.dto.RequestOutputDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;

@UtilityClass
public class ItemRequestMapper {
    public RequestOutputDto toRequestOutputDto(ItemRequest request) {
        return new RequestOutputDto(request.getId(),
                request.getDescription(),
                request.getRequester(),
                request.getCreated(),
                new ArrayList<>());
    }

    public ItemRequest toRequest(RequestInputDto requestInputDto, Long userId) {
        return new ItemRequest(0L,
                requestInputDto.getDescription(),
                userId,
                LocalDateTime.now());
    }
}
