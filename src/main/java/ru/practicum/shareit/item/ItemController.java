package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validators.Create;
import ru.practicum.shareit.validators.Update;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Validated(Create.class) @RequestBody StandardItemDto itemDto) {
        log.info("Выполнен запрос POST /items.");
        return service.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Validated(Update.class) @RequestBody StandardItemDto itemDto) {
        log.info("Выполнен запрос PATCH /items/{}.", itemId);
        return service.update(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Выполнен запрос GET /items/{}.", itemId);
        return service.getItemIdByOwnerId(itemId, ownerId);
    }

    @GetMapping
    public List<ItemDto> get(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Выполнен запрос GET /items.");
        return service.get(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(required = false, name = "text", defaultValue = "") String text) {
        log.info("Выполнен запрос GET /items/search?text={}.", text);
        if (text.isBlank()) {
            return List.of();
        } else {
            return service.search(text);
        }
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Validated(Create.class) @RequestBody CommentRequestDto commentRequestDto) {
        log.info("Выполнен запрос POST /{}/comment.", itemId);
        return service.addComment(itemId, userId, commentRequestDto);
    }
}