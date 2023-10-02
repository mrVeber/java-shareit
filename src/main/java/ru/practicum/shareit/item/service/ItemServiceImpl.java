package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private static final Sort SORT_START_DESC = Sort.by(DESC, "start");
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<ItemDtoBooking> getUserItems(long userId, Pageable pageable) {
        List<Item> userItems = itemRepository.findAllByOwnerIdOrderByIdAsc(userId, pageable);
        return setAllBookingsAndComments(userId, userItems);
    }

    @Override
    public ItemDtoBooking getById(long itemId, long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(()
                -> new NotFoundException("Item with id = " + itemId + " not found"));
        return setAllBookingsAndComments(userId, Collections.singletonList(item)).get(0);
    }

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {

        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("User not found");
        });

        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(() ->
                    new NotFoundException("Request not found"));
        }
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, user, itemRequest));
        itemDto.setId(item.getId());
        return itemDto;
    }

    @Override
    public ItemDto update(long itemId, ItemDto itemDto, long ownerId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException("Item not found for update");
        });
        if (item.getOwner().getId() == ownerId) {
            if ((itemDto.getName() != null) && (!item.getName().isBlank())) {
                item.setName(itemDto.getName());
            }
            if ((itemDto.getDescription() != null) && (!item.getDescription().isBlank())) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            itemRepository.save(item);
        } else {
            throw new NotFoundException("Item not found for update");
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public CommentDto createComment(long itemId, CommentDto commentDto, long authorId) {

        Item item = itemRepository.findById(itemId).orElseThrow(()
                -> new NotFoundException("Item with id = " + itemId + " not found"));
        User user = userRepository.findById(authorId).orElseThrow(()
                -> new NotFoundException("User with id = " + authorId + " not found"));

        Comment comment = CommentMapper.toComment(user, item, commentDto);

        if (!bookingRepository.findByBookerIdAndItemIdAndEndBefore(
                authorId,
                itemId,
                LocalDateTime.now(),
                SORT_START_DESC).isEmpty()) {
            comment.setItem(item);
            comment.setAuthorId(user);
            comment.setCreated(LocalDateTime.now());
            commentRepository.save(comment);
            return CommentMapper.toCommentDto(comment);
        } else throw new BadRequestException("This User can not send comment to this Item");
    }

    @Override
    public List<ItemDto> search(String text, Pageable pageable) {
        return itemRepository.searchByText(text.toLowerCase(), pageable)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(long id) {
        itemRepository.deleteById(id);
    }

    private List<ItemDtoBooking> setAllBookingsAndComments(long userId, List<Item> items) {
        LocalDateTime now = LocalDateTime.now();
        List<Long> ids = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        Map<Long, BookingDtoResponse> last = bookingRepository.findBookingsLast(ids, now, userId, SORT_START_DESC).stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toMap(BookingDtoResponse::getItemId, item -> item, (a, b) -> a));
        Map<Long, BookingDtoResponse> next = bookingRepository.findBookingsNext(ids, now, userId, SORT_START_DESC).stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toMap(BookingDtoResponse::getItemId, item -> item, (a, b) -> b));
        List<ItemDtoBooking> result = items.stream()
                .map(ItemMapper::toItemDtoBooking)
                .collect(toList());
        Map<Long, List<Comment>> comments = commentRepository.findByItemId_IdIn(ids).stream()
                .collect(groupingBy(comment -> comment.getItem().getId()));
        result.forEach(item -> {
            item.setLastBooking(last.get(item.getId()));
            item.setNextBooking(next.get(item.getId()));
            item.getComments().addAll(comments.getOrDefault(item.getId(), List.of()).stream()
                    .map(CommentMapper::toCommentDto).collect(toList()));
        });
        return result;
    }
}
