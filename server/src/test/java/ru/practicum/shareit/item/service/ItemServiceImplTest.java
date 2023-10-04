//package ru.practicum.shareit.item.service;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.PageRequest;
//import ru.practicum.shareit.booking.model.BookingStatus;
//import ru.practicum.shareit.booking.model.Booking;
//import ru.practicum.shareit.booking.repository.BookingRepository;
//import ru.practicum.shareit.item.model.Comment;
//import ru.practicum.shareit.item.mapper.CommentMapper;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.item.mapper.ItemMapper;
//import ru.practicum.shareit.item.repository.CommentRepository;
//import ru.practicum.shareit.item.repository.ItemRepository;
//import ru.practicum.shareit.request.model.ItemRequest;
//import ru.practicum.shareit.request.repository.ItemRequestRepository;
//import ru.practicum.shareit.user.model.User;
//import ru.practicum.shareit.user.repository.UserRepository;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class ItemServiceImplTest {
//    @Mock
//    CommentRepository commentRepository;
//    @Mock
//    ItemRepository itemRepository;
//    @Mock
//    UserRepository userRepository;
//    @Mock
//    BookingRepository bookingRepository;
//    @Mock
//    ItemRequestRepository itemRequestRepository;
//    @InjectMocks
//    ItemServiceImpl itemService;
//    @Captor
//    ArgumentCaptor<Item> itemArgumentCaptor;
//
//    @Test
//    void findAllTest() {
//        PageRequest p = PageRequest.of(0, 20);
//        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong(), any()))
//                .thenReturn(Collections.emptyList());
//        assertTrue(itemService.getUserItems(1L, p).isEmpty());
//    }
//
//    @Test
//    void findItem_whenItemFound() {
//        ItemDtoBooking expectedItem = new ItemDtoBooking();
//        expectedItem.setComments(new ArrayList<>());
//        when(itemRepository.findById(1L)).thenReturn(Optional.of(new Item()));
//
//        ItemDtoBooking actualItem = itemService.getById(1L, 1L);
//
//        assertEquals(expectedItem, actualItem);
//    }
//
//    @Test
//    void findItem_whenItemNotFound_thenObjectNotFoundThrown() {
//        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
//
//        NotFoundException ex = assertThrows(NotFoundException.class, () -> itemService.getById(1L, 1L));
//        assertEquals("Item with id = 1 not found", ex.getMessage());
//    }
//
//    @Test
//    void create_whenItemValid_thenItemSaved() {
//        User user = new User(
//                1L,
//                "name",
//                "email@email.ru");
//        ItemRequest itemRequest = new ItemRequest(
//                1L,
//                "desc",
//                user,
//                LocalDateTime.now()
//        );
//        ItemDto expectedItemDto = new ItemDto(
//                1L,
//                "name",
//                "description",
//                true,
//                1L);
//        Item item = new Item(
//                1L,
//                "name",
//                "description",
//                true,
//                user,
//                itemRequest);
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//        when(itemRepository.save(item)).thenReturn(item);
//        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
//
//        ItemDto actualItemDto = itemService.create(1L, expectedItemDto);
//
//        assertEquals(expectedItemDto, actualItemDto);
//        verify(itemRepository).save(item);
//    }
//
//    @Test
//    void create_whenUserNotFound() {
//        when(userRepository.findById(1L)).thenReturn(Optional.empty());
//
//        NotFoundException ex = assertThrows(NotFoundException.class, () -> itemService.create(1L, new ItemDto()));
//        assertEquals("User not found", ex.getMessage());
//    }
//
//    @Test
//    void update_whenItemForUpdateValid_thenItemUpdated() {
//        User user = new User(
//                1L,
//                "name",
//                "email@email.ru");
//        Item oldItem = new Item(
//                1L,
//                "name",
//                "description",
//                true,
//                user,
//                null);
//        ItemDto itemForUpdate = new ItemDto(
//                1L,
//                "new name",
//                "new description",
//                true,
//                null);
//        when(itemRepository.findById(1L)).thenReturn(Optional.of(oldItem));
//        when(itemRepository.save(oldItem)).thenReturn(ItemMapper.toItem(itemForUpdate, user, null));
//
//        ItemDto actual = itemService.update(1L, itemForUpdate, 1L);
//
//        verify(itemRepository).save(itemArgumentCaptor.capture());
//        Item saved = itemArgumentCaptor.getValue();
//
//        assertEquals(actual.getId(), saved.getId());
//        assertEquals(actual.getName(), saved.getName());
//        assertEquals(actual.getDescription(), saved.getDescription());
//        assertEquals(actual.getAvailable(), saved.getAvailable());
//        assertNull(actual.getRequestId());
//    }
//
//    @Test
//    void update_whenItemForUpdateNotByOwner() {
//        User user = new User(
//                1L,
//                "name",
//                "email@email.ru");
//        User notOwner = new User(
//                2L,
//                "name",
//                "email@email.ru");
//        Item item = new Item(
//                1L,
//                "name",
//                "description",
//                true,
//                user,
//                null);
//
//        NotFoundException ex = assertThrows(NotFoundException.class, () ->
//                itemService.update(notOwner.getId(), new ItemDto(), 1L));
//        assertEquals("Item not found for update", ex.getMessage());
//    }
//
//    @Test
//    void searchItem_whenTextNotBlank() {
//        User user = new User(
//                1L,
//                "name",
//                "email@email.ru");
//        Item item = new Item(
//                1L,
//                "name",
//                "Какая-то дрель",
//                true,
//                user,
//                null);
//        when(itemRepository.searchByText(anyString(), any())).thenReturn(Collections.singletonList(item));
//        PageRequest p = PageRequest.of(0, 20);
//
//        List<ItemDto> actual = itemService.search("дрель", p);
//        assertEquals(1, actual.size());
//        assertEquals(ItemMapper.toItemDto(item), actual.get(0));
//    }
//
//    @Test
//    void searchItem_whenTextIsBlank() {
//        when(itemRepository.searchByText(anyString(), any())).thenReturn(Collections.emptyList());
//        PageRequest p = PageRequest.of(0, 20);
//
//        List<ItemDto> actual = itemService.search("дрель", p);
//        assertTrue(actual.isEmpty());
//    }
//
//    @Test
//    void addComment_whenCommentValid_thenSaved() {
//        User user = new User(
//                1L,
//                "name",
//                "email@email.ru");
//        Item item = new Item(
//                1L,
//                "name",
//                "Какая-то дрель",
//                true,
//                user,
//                null);
//        Booking booking = new Booking(
//                1L,
//                LocalDateTime.now().minusHours(2),
//                LocalDateTime.now().minusHours(1),
//                item,
//                user,
//                BookingStatus.APPROVED);
//        CommentDto commentDto = new CommentDto();
//        commentDto.setId(1L);
//        commentDto.setText("Хорошая дрель");
//        commentDto.setAuthorName(user.getName());
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
//        when(bookingRepository.findByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any(), any()))
//                .thenReturn(List.of(booking));
//
//        Comment forSend = CommentMapper.toComment(user, item, commentDto);
//        CommentDto actual = itemService.createComment(1L, commentDto, 1L);
//        forSend.setCreated(actual.getCreated());
//
//        verify(commentRepository).save(forSend);
//        assertEquals(forSend.getId(), actual.getId());
//        assertEquals(forSend.getText(), actual.getText());
//        assertEquals(forSend.getAuthorId().getName(), actual.getAuthorName());
//    }
//
//    @Test
//    void addComment_whenUserHasNotBooking() {
//        User user = new User(
//                1L,
//                "name",
//                "email@email.ru");
//        Item item = new Item(
//                1L,
//                "name",
//                "Какая-то дрель",
//                true,
//                user,
//                null);
//        CommentDto commentDto = new CommentDto();
//        commentDto.setId(1L);
//        commentDto.setText("Хорошая дрель");
//        commentDto.setAuthorName(user.getName());
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
//        when(bookingRepository.findByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any(), any()))
//                .thenThrow(new BadRequestException("You can't make a comment to this item"));
//
//        BadRequestException ex = assertThrows(BadRequestException.class, () ->
//                itemService.createComment(1L, commentDto, 1L));
//        assertEquals("You can't make a comment to this item", ex.getMessage());
//    }
//}
//
