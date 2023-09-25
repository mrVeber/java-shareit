package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private final long testOwnerId = 10;
    private final User owner = User.builder().id(testOwnerId).name("Test").email("test@mail.com").build();

    private final long testAuthorId = 11;
    private final String testAuthorName = "author";
    private final User author = User.builder().id(testAuthorId).name(testAuthorName).email("author@mail.com").build();

    private final long testOneBookerId = 12;
    private final User oneBooker = User.builder().id(testOneBookerId).name("oneBooker").email("oneBooker@mail.com").build();

    private final long testTwoBookerId = 13;
    private final User twoBooker = User.builder().id(testTwoBookerId).name("twoBooker").email("twoBooker@mail.com").build();

    private final long testItemId = 5;
    private final ItemDtoRequest requestItemDto = ItemDtoRequest.builder().name("Test").description("Test")
            .available(true).requestId(0).build();
    private final ItemDtoRequest newDataItemDto = ItemDtoRequest.builder().name("New").description("New")
            .available(true).requestId(0).build();

    private final Item newDataItem = Item.builder().id(testItemId).name("New").description("New").available(true).build();

    private final Item item = Item.builder().id(testItemId).name("Test").description("TestTestTestTestTest")
            .available(true).owner(owner).build();
    private final ItemDtoResponse responseItemDto = ItemDtoResponse.builder().id(testItemId).name("Test")
            .description("TestTestTestTestTest").available(true).requestId(0).build();
    private final ItemDtoResponse newResponseItemDto = ItemDtoResponse.builder().id(testItemId).name("New")
            .description("New").available(true).build();
    private final ItemDtoFullResponse fullResponseItemDto = ItemDtoFullResponse.builder().id(testItemId).name("Test")
            .description("TestTestTestTestTest").available(true).build();

    private final long testItemRequestId = 105;
    private final ItemRequest itemRequest = ItemRequest.builder().id(testItemRequestId).description("TestTestTestTestTest")
            .created(LocalDateTime.of(2022, 12, 12, 12, 12)).build();

    private final long testCommentId = 205;
    private final CommentDtoRequest requestCommentDto = CommentDtoRequest.builder().text("TestTestTestTestTest").build();
    private final Comment comment = Comment.builder().id(testCommentId).text("TestTestTestTestTest")
            .created(LocalDateTime.of(2023, 9, 1, 12, 0)).item(item).author(author).build();
    private final CommentDtoResponse responseCommentDto = CommentDtoResponse.builder().id(testCommentId)
            .text("TestTestTestTestTest")
            .created(LocalDateTime.of(2023, 9, 1, 12, 0)).authorName(testAuthorName).build();

    private final long testOneBookingId = 301;
    private final Booking oneBooking = Booking.builder().id(testOneBookingId)
            .start(LocalDateTime.of(2023, 9, 1, 12, 0))
            .end(LocalDateTime.of(2023, 9, 30, 12, 0)).status(BookingStatus.APPROVED)
            .item(item).booker(oneBooker).build();
    private final ItemBookingDto oneBookingDto = ItemBookingDto.builder().id(testOneBookingId)
            .start(LocalDateTime.of(2023, 9, 1, 12, 0))
            .end(LocalDateTime.of(2023, 9, 30, 12, 0)).status(BookingStatus.APPROVED)
            .itemId(testItemId).bookerId(testOneBookerId).build();

    private final long testTwoBookingId = 302;
    private final Booking twoBooking = Booking.builder().id(testTwoBookingId)
            .start(LocalDateTime.of(2023, 10, 1, 12, 0))
            .end(LocalDateTime.of(2023, 10, 31, 12, 0)).status(BookingStatus.APPROVED)
            .item(item).booker(twoBooker).build();
    private final ItemBookingDto twoBookingDto = ItemBookingDto.builder().id(testTwoBookingId)
            .start(LocalDateTime.of(2023, 10, 1, 12, 0))
            .end(LocalDateTime.of(2023, 10, 31, 12, 0)).status(BookingStatus.APPROVED)
            .itemId(testItemId).bookerId(testTwoBookerId).build();

    @Test
    void testCreateItemWithoutRequest() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        Mockito.when(itemRepository.save(Mockito.any()))
                .thenReturn(item);

        assertEquals(itemService.create(requestItemDto, testOwnerId), responseItemDto);
    }

    @Test
    void testCreateItemWithRequest() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        Mockito.when(itemRepository.save(Mockito.any()))
                .thenReturn(item);

        assertEquals(itemService.create(requestItemDto, testOwnerId), responseItemDto);
    }

    @Test
    void testGetItemById() {
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito.when(commentRepository.findByItemId(Mockito.anyLong()))
                .thenReturn(null);
        Mockito.when(bookingRepository.findByItemId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(null);

        assertEquals(itemService.getById(testItemId, testOwnerId), fullResponseItemDto);
    }

    @Test
    void testGetItemByIdWithComments() {
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item));

        List<Comment> comments = new ArrayList<>();
        comments.add(comment);
        Mockito.when(commentRepository.findByItemId(Mockito.anyLong()))
                .thenReturn(comments);
        List<CommentDtoResponse> responseComments = new ArrayList<>();
        responseComments.add(responseCommentDto);
        fullResponseItemDto.setComments(responseComments);

        Mockito.when(bookingRepository.findByItemId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(null);

        assertEquals(itemService.getById(testItemId, testOwnerId), fullResponseItemDto);
    }

    @Test
    void getItemByIdWithCommentsWithBookingsTest() {
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item));

        List<Comment> comments = new ArrayList<>();
        comments.add(comment);
        Mockito.when(commentRepository.findByItemId(Mockito.anyLong()))
                .thenReturn(comments);
        List<CommentDtoResponse> responseComments = new ArrayList<>();
        responseComments.add(responseCommentDto);
        fullResponseItemDto.setComments(responseComments);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(oneBooking);
        bookings.add(twoBooking);
        Mockito.when(bookingRepository.findByItemId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(bookings);
        fullResponseItemDto.setLastBooking(oneBookingDto);
        fullResponseItemDto.setNextBooking(twoBookingDto);
        System.out.println("!!! " + itemService.getById(testItemId, testOwnerId));
        System.out.println("!!! " + fullResponseItemDto);
        assertEquals(itemService.getById(testItemId, testOwnerId), fullResponseItemDto);
    }

    @Test
    void testGetItemsOneOwner() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(owner));


        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        Page<Item> items = new PageImpl<>(itemList);

        Mockito.when(itemRepository.findAllByOwnerId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(items);

        Mockito.when(commentRepository.findAllByItemIdIn(Mockito.anyList(), Mockito.any()))
                .thenReturn(new ArrayList<>());

        Mockito.when(bookingRepository.findAllByItemIdInAndStatus(Mockito.anyList(), Mockito.any(), Mockito.any()))
                .thenReturn(new ArrayList<>());

        List<ItemDtoFullResponse> responseItemList = new ArrayList<>();
        responseItemList.add(fullResponseItemDto);

        assertEquals(itemService.getItemsOneOwner(testItemId, 0, 10),  responseItemList);
    }

    @Test
    void testGetItemsOneOwnerWithComments() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(owner));


        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        Page<Item> items = new PageImpl<>(itemList);

        Mockito.when(itemRepository.findAllByOwnerId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(items);

        List<Comment> comments = new ArrayList<>();
        comments.add(comment);
        Mockito.when(commentRepository.findAllByItemIdIn(Mockito.anyList(), Mockito.any()))
                .thenReturn(comments);
        List<CommentDtoResponse> responseComments = new ArrayList<>();
        responseComments.add(responseCommentDto);
        fullResponseItemDto.setComments(responseComments);

        Mockito.when(bookingRepository.findAllByItemIdInAndStatus(Mockito.anyList(), Mockito.any(), Mockito.any()))
                .thenReturn(new ArrayList<>());

        List<ItemDtoFullResponse> responseItemList = new ArrayList<>();
        responseItemList.add(fullResponseItemDto);

        assertEquals(itemService.getItemsOneOwner(testItemId, 0, 10),  responseItemList);
    }

    @Test
    void getItemsOneOwnerWithCommentsWithBookingsTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(owner));

        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        Page<Item> items = new PageImpl<>(itemList);

        Mockito.when(itemRepository.findAllByOwnerId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(items);

        List<Comment> comments = new ArrayList<>();
        comments.add(comment);
        Mockito.when(commentRepository.findAllByItemIdIn(Mockito.anyList(), Mockito.any()))
                .thenReturn(comments);
        List<CommentDtoResponse> responseComments = new ArrayList<>();
        responseComments.add(responseCommentDto);
        fullResponseItemDto.setComments(responseComments);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(oneBooking);
        bookings.add(twoBooking);
        Mockito.when(bookingRepository.findAllByItemIdInAndStatus(Mockito.anyList(), Mockito.any(), Mockito.any()))
                .thenReturn(bookings);

        fullResponseItemDto.setLastBooking(oneBookingDto);
        fullResponseItemDto.setNextBooking(twoBookingDto);
        List<ItemDtoFullResponse> responseItemList = new ArrayList<>();
        responseItemList.add(fullResponseItemDto);
        System.out.println(itemService.getItemsOneOwner(testItemId, 0, 10));
        assertEquals(itemService.getItemsOneOwner(testItemId, 0, 10),  responseItemList);
    }

    @Test
    void testUpdateItem() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(owner));

        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item));

        Mockito.when(itemRepository.save(Mockito.any()))
                .thenReturn(newDataItem);

        assertEquals(itemService.update(testItemId, newDataItemDto, testOwnerId), newResponseItemDto);
    }

    @Test
    void testDeleteItem() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item));
        itemService.delete(testItemId, testOwnerId);

        Mockito.verify(itemRepository, Mockito.times(1)).delete(item);
    }

    @Test
    void testSearchItems() {

        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        Page<Item> items = new PageImpl<>(itemList);
        Mockito.when(itemRepository.search(Mockito.anyString(), Mockito.any()))
                .thenReturn(items);
        List<ItemDtoResponse> responseItemList = new ArrayList<>();
        responseItemList.add(responseItemDto);
        assertEquals(itemService.search("test", 0, 10),  responseItemList);
    }

    @Test
    void testCreateComment() {
        Mockito.when(bookingRepository.existsAllByBookerIdAndStatusAndEndBefore(Mockito.anyLong(), Mockito.any(),
                Mockito.any())).thenReturn(true);
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(author));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito.when(commentRepository.save(Mockito.any()))
                .thenReturn(comment);

        assertEquals(itemService.createComment(testItemId, requestCommentDto, testOneBookerId), responseCommentDto);
    }

    @Test
    void testSearchItem() {
        String text = "Утюг";
        item.setName(text);
        responseItemDto.setName(text);

        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        Page<Item> items = new PageImpl<>(itemList);
        Mockito.when(itemRepository.search(Mockito.any(), Mockito.any()))
                .thenReturn(items);

        assertEquals(itemService.search(text, 0, 10), List.of(responseItemDto));
    }
}
