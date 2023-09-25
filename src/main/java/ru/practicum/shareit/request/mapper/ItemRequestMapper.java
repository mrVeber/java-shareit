package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequest toItemRequest(ItemRequestDtoRequest requestDto, User user, LocalDateTime currentMoment) {
        return ItemRequest.builder()
                .description(requestDto.getDescription())
                .created(currentMoment)
                .requestor(user)
                .build();
    }

    public ItemRequestDtoResponse toResponseItemRequestDto(ItemRequest request, UserDtoResponse requestorDto) {
        return ItemRequestDtoResponse.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .requestor(requestorDto)
                .build();
    }

    public ItemRequestDtoFullResponse toFullResponseItemRequestDto(ItemRequest request) {
        return ItemRequestDtoFullResponse.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .build();
    }
}
