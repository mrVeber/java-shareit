package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.model.*;

import ru.practicum.shareit.item.dto.CommentDtoInput;
import ru.practicum.shareit.item.dto.CommentDtoOutput;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;


import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.*;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository requestRepository;
    Sort sort = Sort.by(Sort.Direction.DESC, "id");

    @Transactional
    public ItemDto createItem(ItemDto itemDto, long userId) {

        User owner = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Владелец вещи не найден");
        });
        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            request = requestRepository.findById(itemDto.getRequestId()).orElseThrow(() ->
                    new NotFoundException("Request не найден"));
        }
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner.getId());
        item.setRequest(request);
        ItemDto itemDtoForReturn = ItemMapper.toItemDto(itemRepository.save(item));
        log.info("ItemService - метод createItem (). Добавлен Item {}.", itemDtoForReturn.toString());
        return itemDtoForReturn;
    }

    @Transactional
    public ItemDto update(ItemDto itemDto, long itemId, Long userIdInHeader) {
        Item item = itemRepository.getById(itemId);
        checkEqualsUsersIds(item.getOwner(), userIdInHeader);
        prepareItemForUpdate(item, itemDto);
        log.info("ItemService - update(). Обновлен {}", item.toString());
        return ItemMapper.toItemDto(item);
    }

    public List<ItemWithBookingAndCommentsDto> getAllByUserId(long ownerId, int from, int size) {
        Page<Item> itemsPage = itemRepository.findAllByOwnerOrderByIdAsc(ownerId, PageRequest.of(from, size));
        List<Item> items = itemsPage.toList();
        return addBookingsAndCommentsToList(items);
    }

    public ItemWithBookingAndCommentsDto getById(long id, long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item не найден "));
        if (item.getOwner() == userId) {
            log.info("ItemService - getById(). Возвращен {}", item.toString());
            return addBookingsAndComment(item);
        }
        return addComments(item);
    }

    public List<ItemDto> search(String text, int from, int size) {
        Page<Item> items = itemRepository.search(text, PageRequest.of(from, size));
        List<Item> itemList = items.toList();
        List<ItemDto> itemsDto = itemList.stream().map(ItemMapper::toItemDto).collect(toList());
        log.info("ItemController - search(). Возвращен список из {} предметов", itemList.size());
        return itemsDto;
    }


    @Transactional
    public CommentDtoOutput createComment(CommentDtoInput commentDto, Long userId, Long itemId) {
        commentDto.setCreated(LocalDateTime.now());
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь не найден");
        });
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException("Предмет не найден");
        });

        if (bookingRepository.findLastBookings(itemId, userId, LocalDateTime.now()).isEmpty()) {
            throw new AvailableCheckException(
                    " Добавить комментарий не возможно");
        }
        log.info("ItemController -  createComment(). Добавлен комментарий {}", commentDto);
        return CommentMapper.toCommentDtoOutput(commentRepository.save(CommentMapper
                .toComment(commentDto, user, item)));
    }


    private Item prepareItemForUpdate(Item item, ItemDto itemDto) {
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        log.info("ItemService - Было {} , Стало {}", item.toString(), itemDto.toString());
        return item;
    }

    private void checkEqualsUsersIds(long idInDb, long userIdInHeader) {
        if (idInDb != userIdInHeader) {
            throw new NotFoundException("Id пользователя не соответствует");
        }
    }

    private ItemWithBookingAndCommentsDto addComments(Item item) {
        ItemWithBookingAndCommentsDto itemWithBookingAndCommentsDto = ItemMapper.toItemWithBookingAndCommentsDto(item);
        itemWithBookingAndCommentsDto.setComments(commentRepository.findAllByItemId(item.getId())
                .stream().map(CommentMapper::toCommentDtoOutput)
                .collect(toList()));
        return itemWithBookingAndCommentsDto;
    }

    private ItemWithBookingAndCommentsDto addBookingsAndComment(Item item) {
        ItemWithBookingAndCommentsDto itemWithBookingAndCommentsDto = ItemMapper.toItemWithBookingAndCommentsDto(item);

        Optional<Booking> next = bookingRepository
                .findFirstByItemAndStatusLikeAndStartAfterOrderByStartAsc(item, BookingStatus.APPROVED, LocalDateTime.now());
        Optional<Booking> last = bookingRepository
                .findFirstByItemAndStatusLikeAndStartLessThanEqualOrderByStartDesc(item, BookingStatus.APPROVED, LocalDateTime.now());

        // добавляем в Item прошлый и следующий букинги
        if (next.isPresent()) {
            BookingShortDto bookingShortDtoNext = BookingMapper.toBookingShortDto(next.get());
            itemWithBookingAndCommentsDto.setNextBooking(bookingShortDtoNext);
        }

        if (last.isPresent()) {
            BookingShortDto bookingShortDtoLast =
                    BookingMapper.toBookingShortDto(last.get());
            itemWithBookingAndCommentsDto.setLastBooking(bookingShortDtoLast);
        }

        // добавляем комментарии
        itemWithBookingAndCommentsDto.setComments(commentRepository.findAllByItemId(item.getId())
                .stream().map(CommentMapper::toCommentDtoOutput)
                .collect(toList()));
        return itemWithBookingAndCommentsDto;
    }

    private List<ItemWithBookingAndCommentsDto> addBookingsAndCommentsToList(List<Item> items) {

        List<ItemWithBookingAndCommentsDto> forReturn = items.stream().map(ItemMapper::toItemWithBookingAndCommentsDto).collect(toList());

        Map<Item, List<Comment>> comments = commentRepository.findByItemIn(items, sort).stream().collect(groupingBy(Comment::getItem, toList()));

        Map<Item, Booking> lasts = bookingRepository.findFirstByItemInAndAndStartLessThanEqualAndStatusEqualsOrderByStartDesc(items, LocalDateTime.now(), BookingStatus.APPROVED)
                .stream().collect(toMap(Booking::getItem, identity()));

        Map<Item, Booking> nexts = bookingRepository.findFirstByItemInAndAndStartAfterAndStatusEqualsOrderByStartAsc(items, LocalDateTime.now(), BookingStatus.APPROVED)
                .stream().collect(toMap(Booking::getItem, identity()));

        for (int i = 0; i < items.size(); i++) {

            if (!comments.isEmpty() && comments.containsKey(items.get(i))) {
                List<CommentDtoOutput> comm = comments.get(items.get(i)).stream().map(CommentMapper::toCommentDtoOutput).collect(toList());
                forReturn.get(i).setComments(comm);
            }
            if (!lasts.isEmpty() && lasts.containsKey(items.get(i))) {
                forReturn.get(i).setLastBooking(BookingMapper.toBookingShortDto(lasts.get(items.get(i))));
            }
            if (!nexts.isEmpty() && nexts.containsKey(items.get(i))) {
                forReturn.get(i).setNextBooking(BookingMapper.toBookingShortDto(nexts.get(items.get(i))));
            }
        }
        return forReturn;
    }
}