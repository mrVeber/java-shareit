package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validators.Create;
import ru.practicum.shareit.validators.Update;

import javax.validation.constraints.Min;
import java.util.List;
import javax.validation.Valid;

import static ru.practicum.shareit.utils.Constants.X_SHARER_USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDtoResponse createItem(@RequestHeader(X_SHARER_USER_ID) long userId,
                                      @RequestBody @Validated(Create.class) ItemDtoRequest item) {
        log.info("");
        log.info("Добавление новой вещи: {}", item);
        return itemService.create(item, userId);
    }

    @GetMapping("/{id}")
    public ItemDtoFullResponse getItemById(@RequestHeader(X_SHARER_USER_ID) long userId,
                                           @PathVariable long id) {
        log.info("");
        log.info("Получение данных вещи с id = {}", id);
        return itemService.getById(id, userId);
    }

    @GetMapping
    public List<ItemDtoFullResponse> getItemsOneUser(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                     @RequestParam(defaultValue = "0") @Min(0) int from,
                                                     @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("");
        log.info("Поиск всех вещей, созданных пользователем с id = {}", userId);
        return itemService.getItemsOneOwner(userId, from, size);
    }

    @PatchMapping("/{id}")
    public ItemDtoResponse updateItem(@RequestHeader(X_SHARER_USER_ID) long userId,
                                      @PathVariable long id,
                                      @RequestBody @Validated(Update.class) ItemDtoRequest item) {
        log.info("");
        log.info("Обновление данных вещи с id = {}: {}", id, item);
        return itemService.update(id, item, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@RequestHeader(X_SHARER_USER_ID) long userId,
                           @PathVariable long id) {
        log.info("");
        log.info("Удаление вещи c id = {}", id);
        itemService.delete(id, userId);
    }

    @GetMapping("/search")
    public List<ItemDtoResponse> searchItems(@RequestHeader(X_SHARER_USER_ID) long userId,
                                             @RequestParam String text,
                                             @RequestParam(defaultValue = "0") @Min(0) int from,
                                             @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("");
        log.info("Поиск вещей по определённому запросу пользователя");
        return itemService.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse createComment(@RequestHeader(X_SHARER_USER_ID) long userId,
                                            @PathVariable long itemId,
                                            @RequestBody @Valid CommentDtoRequest comment) {
        log.info("");
        log.info("Добавление для вещи с id = {} нового комментария: {}", itemId, comment);
        return itemService.createComment(itemId, comment, userId);
    }
}