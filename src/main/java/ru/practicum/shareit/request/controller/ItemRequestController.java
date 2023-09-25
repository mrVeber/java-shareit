package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.service.ItemRequestService;
import javax.validation.Valid;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.utils.Constants.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoResponse createItemRequest(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                    @RequestBody @Valid ItemRequestDtoRequest request) {
        log.info("");
        log.info("От пользователя с id = {} добавление нового запроса на вещь: {}", userId, request);
        return itemRequestService.create(request, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoFullResponse getRequestsById(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                      @PathVariable @Positive long requestId) {
        log.info("");
        log.info("Получение данных запроса по id = {} ", requestId);
        return itemRequestService.getById(requestId, userId);
    }

    @GetMapping
    public List<ItemRequestDtoFullResponse> getOwnItemRequests(@RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("");
        log.info("Поиск пользователем с id = {} всех своих запросов с ответами на них", userId);
        return itemRequestService.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoFullResponse> getAllRequests(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                           @RequestParam(defaultValue = "0")  @Min(0) int from,
                                                           @RequestParam(defaultValue = "10")  @Min(1) int size) {
        log.info("");
        log.info("Поиск всех запросов пользователей, на которые можно предложить свои вещи");
        return itemRequestService.getAll(userId, from, size);
    }
}