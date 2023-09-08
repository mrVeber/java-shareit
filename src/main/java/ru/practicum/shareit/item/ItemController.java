package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String LINE = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader(LINE) long userId, @PathVariable long itemId) {
        return itemService.getItemDtoById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader(LINE) long ownerId) {
        return itemService.getAllUserItems(ownerId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(LINE) long ownerId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.add(ownerId, ItemMapper.toItem(itemDto));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader(LINE) long userId, @RequestParam(name = "text") String text) {
        return itemService.searchItems(userId, text);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader(LINE) long ownerId, @PathVariable long itemId) {
        itemService.delete(ownerId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(LINE) long ownerId, @PathVariable long itemId,
                          @RequestBody @NotNull Map<String, String> updates) {
        return itemService.update(ownerId, itemId, updates);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(LINE) long userId, @PathVariable long itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}