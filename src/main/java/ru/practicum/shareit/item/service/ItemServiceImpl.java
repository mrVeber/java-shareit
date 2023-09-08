package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto add(Long ownerId, Item item) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("user nor found"));
        item.setOwner(owner);
        log.info("ItemService: add implementation. User ID {}, item ID {}.", ownerId, item.getId());
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto update(Long ownerId, Long itemId, Map<String, String> updates) {
        Item item = getItemById(itemId);
        if (!getOwnerId(itemId).equals(ownerId)) {
            throw new NotFoundException("user mustn't be owner this item");
        }
        if (updates.containsKey("name") && !updates.get("name").isBlank()) {
            String value = updates.get("name");
            item.setName(value);
        }
        if (updates.containsKey("description") && !updates.get("description").isBlank()) {
            String value = updates.get("description");
            item.setDescription(value);
        }
        if (updates.containsKey("available")) {
            item.setAvailable(Boolean.valueOf(updates.get("available")));
        }
        log.info("ItemService: update implementation. User ID {}, itemId {}.", ownerId, itemId);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional(readOnly = true)
    @Override
    public Long getOwnerId(Long itemId) {
        Item item = getItemById(itemId);
        User owner = item.getOwner();
        if (owner == null) throw new NotFoundException("owner item not found");
        log.info("ItemService: getOwnerId implementation. owner ID {}.", itemId);
        return owner.getId();
    }


    @Transactional(readOnly = true)
    @Override
    public Item getItemById(Long itemId) {
        log.info("ItemService: getItemById implementation. item ID {}.", itemId);
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("item not found"));
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getItemDtoById(Long itemId, Long userId) {
        Item item = getItemById(itemId);
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        comments.sort(Comparator.comparing(Comment::getCreated));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (item.getOwner() != null && item.getOwner().getId().equals(userId)) {
            setBookings(itemDto, bookingRepository.findAllByItemIdAndStatus(itemId, BookingStatus.APPROVED));
        }
        setComments(itemDto, comments);
        log.info("ItemService: getItemDtoById implementation. User ID {}, item ID {}.", userId, itemId);
        return itemDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getAllUserItems(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<Booking> bookings = bookingRepository.findAllByOwnerIdAndStatus(userId, BookingStatus.APPROVED);
        List<Comment> comments = commentRepository.findAllByItemIdIn(items.stream()
                .map(Item::getId)
                .collect(Collectors.toList()));
        comments.sort(Comparator.comparing(Comment::getCreated));
        List<ItemDto> listItemDto = ItemMapper.toItemDtoList(items);
        listItemDto.forEach(i -> {
            setBookings(i, bookings);
            setComments(i, comments);
        });
        log.info("ItemService: getAllUserItems implementation. User ID {}.", userId);
        return listItemDto;
    }

    @Transactional
    @Override
    public void delete(Long ownerId, Long itemId) {
        Item item = getItemById(itemId);
        if (!getOwnerId(itemId).equals(ownerId)) {
            throw new NotFoundException("user not owner");
        }
        log.info("ItemService: delete implementation. Item ID {}.", itemId);
        itemRepository.delete(item);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> searchItems(Long userId, String query) {
        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }
        log.info("ItemService: searchItems implementation. User ID {}.", userId);
        return ItemMapper.toItemDtoList(itemRepository.searchAvailableItems(query));
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found."));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("item not found."));
        bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId, BookingStatus.APPROVED,
                        LocalDateTime.now())
                .orElseThrow(() -> new ValidationException("User not booking."));
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        log.info("ItemService: addComment implementation. Item ID {}, User ID {} .", itemId, userId);
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    private void setBookings(ItemDto itemDto, List<Booking> bookings) {
        itemDto.setNextBooking(bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .filter(booking -> booking.getItem().getId().equals(itemDto.getId()))
                .sorted(Comparator.comparing(Booking::getStart))
                .map(BookingMapper::toBookingDto)
                .limit(1)
                .findFirst().orElse(null));
        itemDto.setLastBooking(bookings.stream()
                .filter(booking -> booking.getItem().getId().equals(itemDto.getId()))
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(BookingMapper::toBookingDto)
                .limit(1)
                .findFirst().orElse(null));
    }

    private void setComments(ItemDto itemDto, List<Comment> comments) {
        Long itemId = itemDto.getId();
        itemDto.setComments(comments.stream()
                .filter(comment -> comment.getItem().getId().equals(itemId))
                .map(CommentMapper::toDto)
                .collect(Collectors.toList()));
    }
}
