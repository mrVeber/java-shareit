package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class InMemoryItemDao implements ItemDao {
    private final Map<Long, Item> items = new HashMap<>();
    private long itemIdCounter = 1;

    @Override
    public Item addNewItem(Item item) {
        log.debug("Received item to add as new one.");
        long newItemId = generateId();
        item.setItemId(newItemId);
        items.put(newItemId, item);
        log.debug("New item with id {} added successfully.", newItemId);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        log.debug("Received updated item with id {}.", item.getItemId());
        items.put(item.getItemId(), item);
        return item;
    }

    @Override
    public Optional<Item> getItemById(long id) {
        log.debug("Received request to get item with id {}", id);
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> getItemsByUserId(long userId) {
        log.debug("Received request to get all items own by user with id {}", userId);

        return items.values().stream()
                .filter(item -> item.getOwnerId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchInDescription(String text) {
        log.debug("Received request to search items which description contains text \"{}\"", text);
String toUpperCaseText = text.toUpperCase(Locale.ROOT);
        return items.values().stream()
                .filter(item -> item.isAvailable()
                        && item.getDescription().toUpperCase(Locale.ROOT).contains(toUpperCaseText))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserItems(long userId) {
        log.debug("Received request to delete user with id {} items", userId);
        items.remove(userId);
    }

    private Long generateId() {
        return itemIdCounter++;
    }
}
