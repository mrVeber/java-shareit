package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.validators.Create;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.utils.Constant.USER_ID_HEADER;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(name = USER_ID_HEADER) long userId,
            @Validated(Create.class) @RequestBody ItemDto itemDto) {
        log.info("Received a request to add a item");
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(
            @PathVariable long itemId,
            @RequestBody ItemDto itemDto,
            @RequestHeader(name = USER_ID_HEADER) long ownerId) {
        log.info("Received a request to update the item");
        return itemClient.update(ownerId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @PathVariable long itemId,
            @RequestBody @Validated(Create.class) CommentDto commentDto,
            @RequestHeader(name = USER_ID_HEADER) long authorId) {
        log.info("Received a request to update the item");
        return itemClient.createComment(itemId, authorId, commentDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(
            @RequestHeader(name = USER_ID_HEADER) long ownerId,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "20") int size) {
        log.info("Received a request to get Items from User with id = {}", ownerId);
        return itemClient.getUserItems(ownerId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(
            @RequestHeader(name = USER_ID_HEADER) long ownerId,
            @PathVariable long itemId) {
        log.info("Received a request to get Item with id {}", itemId);
        return itemClient.getById(itemId, ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestHeader(name = USER_ID_HEADER) long ownerId,
            @RequestParam String text,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "20") int size) {
        log.info("Received a request to search by query = {}", text);
        return itemClient.search(text, ownerId, from, size);
    }
}
