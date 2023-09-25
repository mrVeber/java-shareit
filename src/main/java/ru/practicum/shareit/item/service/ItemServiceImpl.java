package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.*;
import ru.practicum.shareit.item.dto.*;
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

import static java.util.stream.Collectors.*;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    private User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Пользователь " +
                "с id = %s отсутствует в БД. Выполнить операцию невозможно!", id)));
    }

    private Item getItemById(long id) {
        return itemRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Вещь " +
                "с id = %s отсутствует в БД. Выполнить операцию невозможно!", id)));
    }

    private ItemRequest getItemRequestById(long id) {
        return itemRequestRepository.findById(id).orElseThrow(() -> new NotFoundException(
                String.format("запрос на вещь с id = %s отсутствует в БД. Выполнить операцию невозможно!", id)));
    }

    @Override
    public ItemDtoResponse create(ItemDtoRequest itemDto, long userId) {
        User owner = getUserById(userId);
        Item dataItem = ItemMapper.toItem(itemDto, owner);
        long requestId = itemDto.getRequestId();
        if (requestId > 0) {
            dataItem.setItemRequest(getItemRequestById(requestId));
        }
        Item item = itemRepository.save(dataItem);
        log.info("Данные новой вещи добавлены в БД: {}.", item);

        ItemDtoResponse responseItemDto = ItemMapper.toResponseItemDto(item);
        responseItemDto.setRequestId(requestId);
        log.info("Новая вещь создана: {}.", responseItemDto);
        return responseItemDto;
    }

    @Override
    public ItemDtoFullResponse getById(long id, long ownerId) {
        Item item = getItemById(id);
        log.info("Вещь найдена в БД: {}.", item);

        List<Comment> oneItemComments =  commentRepository.findByItemId(item.getId());
        List<Booking> oneItemBooking =  bookingRepository.findByItemId(item.getId(), Sort.by(ASC, "start"));
        ItemDtoFullResponse itemDto = ItemMapper.toFullResponseItemDto(item);
        setComments(itemDto, oneItemComments);

        if ((item.getOwner().getId() == ownerId) && (oneItemBooking != null)) {
            List<Booking> oneItemBookingByStatus = oneItemBooking
                    .stream()
                    .filter((Booking booking) -> (booking.getStatus().equals(BookingStatus.APPROVED)))
                    .collect(toList());

            setLastAndNextBookings(itemDto, oneItemBookingByStatus, LocalDateTime.now());
        }
        log.info("Все данные вещи получены: {}.", itemDto);
        return itemDto;
    }

    @Override
    public List<ItemDtoFullResponse> getItemsOneOwner(long userId, int from, int size) {
        getUserById(userId);
        log.info("Получение данных всех вещей пользователя из БД.");
        LocalDateTime currentMoment = LocalDateTime.now();

        Pageable pageable = PageRequest.of(from / size, size);
        Page<Item> itemList = itemRepository.findAllByOwnerId(userId, pageable);
        List<Long> itemIdList = itemList.stream().map(Item::getId).collect(toList());

        Map<Item, List<Comment>> allComments = commentRepository.findAllByItemIdIn(itemIdList,
                        Sort.by(DESC, "created"))
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));

        Map<Item, List<Booking>> allBookings = bookingRepository.findAllByItemIdInAndStatus(itemIdList, BookingStatus.APPROVED,
                        Sort.by(ASC, "start"))
                .stream()
                .collect(groupingBy(Booking::getItem, toList()));

        return itemList
                .stream()
                .map((Item item) -> {
                    ItemDtoFullResponse itemDto = ItemMapper.toFullResponseItemDto(item);
                    setComments(itemDto, allComments.get(item));
                    return setLastAndNextBookings(itemDto, allBookings.get(item), currentMoment);
                })
                .collect(toList());
    }

    @Override
    public ItemDtoResponse update(long id, ItemDtoRequest requestItemDto, long userId) {
        getUserById(userId);
        Item item = getItemById(id);
        log.info("Вещь найдена в БД: {}.", item);
        if (item.getOwner().getId() == userId) {
            Item newDataItem = itemRepository.save(ItemMapper.toUpdatedItem(item, requestItemDto));
            log.info("Данные вещи обновлены в БД: {}.", newDataItem);
            ItemDtoResponse responseItemDto = ItemMapper.toResponseItemDto(newDataItem);
            log.info("Данные вещи обновлены: {}.", responseItemDto);
            return responseItemDto;
        } else {
            throw new NotFoundException(String.format("Пользователь с id = %s не является владельцем вещи с id = %s!" +
                    "Обновить её данные невозможно.", userId, id));
        }
    }

    @Override
    public void delete(long id, long userId) {
        getUserById(userId);
        Item item = getItemById(id);
        log.info("Вещь найдена в БД: {}.", item);
        if (item.getOwner().getId() == userId) {
            itemRepository.delete(item);
            log.info("Все данные вещи удалены.");
        } else {
            throw new NotFoundException(String.format("Пользователь с id = %s не является владельцем вещи с id = %s!" +
                    "Удалить её данные невозможно.", userId, id));
        }
    }

    @Override
    public List<ItemDtoResponse> search(String text, int from, int size) {
        List<ItemDtoResponse> itemDtoList = new ArrayList<>();
        if (!text.isBlank()) {
            Pageable pageable = PageRequest.of(from / size, size);
            Page<Item> itemList = itemRepository.search(text, pageable);
            itemDtoList = itemList
                    .stream()
                    .filter(Item::isAvailable)
                    .map(ItemMapper::toResponseItemDto)
                    .collect(toList());
        }
        log.info("Сформирован список всех доступных для аренды вещей в количестве {} штук" +
                " по запросу: {}.", itemDtoList.size(), text);
        return itemDtoList;
    }

    @Override
    public CommentDtoResponse createComment(long itemId, CommentDtoRequest requestCommentDto, long userId) {
        Item item = getItemById(itemId);
        User user = getUserById(userId);
        LocalDateTime currentMoment = LocalDateTime.now();

        if (!bookingRepository.existsAllByBookerIdAndStatusAndEndBefore(userId, BookingStatus.APPROVED, currentMoment)) {
            throw new ValidationException(String.format("Пользователь с id = %s, который хочет добавить комментарий, " +
                    "никогда не бронировал вещь с id = %s. Выполнить операцию невозможно!", userId, itemId));
        }

        Comment commentData = CommentMapper.toComment(item, requestCommentDto, user, currentMoment);
        Comment comment = commentRepository.save(commentData);
        log.info("Данные комментария добавлены в БД: {}.", comment);
        CommentDtoResponse responseCommentDto = CommentMapper.toResponseCommentDto(comment);
        log.info("Новая вещь создана: {}.", responseCommentDto);
        return responseCommentDto;

    }

    private ItemDtoFullResponse setComments(ItemDtoFullResponse itemDto, List<Comment> oneItemComments) {

        if (oneItemComments != null) {
            List<CommentDtoResponse> comments = oneItemComments
                    .stream()
                    .map(CommentMapper::toResponseCommentDto)
                    .collect(toList());
            itemDto.setComments(comments);
        }
        return itemDto;
    }

    private ItemDtoFullResponse setLastAndNextBookings(ItemDtoFullResponse itemDto, List<Booking> oneItemBookings,
                                                       LocalDateTime currentMoment) {
        if (oneItemBookings != null) {
            Booking lastBooking = null;
            Booking nextBooking = null;

            for (Booking booking : oneItemBookings) {
                LocalDateTime bookingStart = booking.getStart();
                if (!bookingStart.isAfter(currentMoment)) {
                    lastBooking = booking;
                }
                if (bookingStart.isAfter(currentMoment)) {
                    nextBooking = booking;
                    break;
                }
            }

            if (lastBooking != null) {
                ItemBookingDto lastBookingDto = BookingMapper.toItemBookingDto(lastBooking);
                itemDto.setLastBooking(lastBookingDto);
            }

            if (nextBooking != null) {
                ItemBookingDto nextBookingDto = BookingMapper.toItemBookingDto(nextBooking);
                itemDto.setNextBooking(nextBookingDto);
            }
        }
        return itemDto;
    }
}