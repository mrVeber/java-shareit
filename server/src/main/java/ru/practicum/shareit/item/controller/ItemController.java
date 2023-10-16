package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(
            @RequestHeader(name = "X-Sharer-User-Id") long userId,
            @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @PathVariable long itemId,
            @RequestBody ItemDto itemDto,
            @RequestHeader(name = "X-Sharer-User-Id") long ownerId) {
        return itemService.update(itemId, itemDto, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(
            @PathVariable long itemId,
            @RequestBody CommentDto commentDto,
            @RequestHeader(name = "X-Sharer-User-Id") long authorId) {
        return itemService.createComment(itemId, commentDto, authorId);
    }

    @GetMapping
    public List<ItemDtoBooking> getUserItems(
            @RequestHeader(name = "X-Sharer-User-Id") long ownerId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(from / size, size);
        return itemService.getUserItems(ownerId, pageable);
    }

    @GetMapping("/{itemId}")
    public ItemDtoBooking getItem(
            @RequestHeader(name = "X-Sharer-User-Id") long ownerId,
            @PathVariable long itemId) {
        return itemService.getById(itemId, ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(
            @RequestHeader(name = "X-Sharer-User-Id") long ownerId,
            @RequestParam String text,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return itemService.search(text, pageRequest);
    }

    @DeleteMapping("/{itemId}")
    public void delete(
            @PathVariable long itemId) {
        itemService.delete(itemId);
    }
}
