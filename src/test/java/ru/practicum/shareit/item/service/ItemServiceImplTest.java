package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDtoInput;
import ru.practicum.shareit.item.dto.CommentDtoOutput;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    void shouldCreateItem() {

        Item item1 = new Item(1L, "item1", "description Item1", true, 2L, null);
        User user1 = new User(1L, "userName", "user@mail.ru");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.save(any())).thenReturn(item1);
        ItemDto itemDto = ItemMapper.toItemDto(item1);
        itemService.createItem(itemDto, user1.getId());
        verify(itemRepository).save(any());
    }

    @Test
    void shouldUpdateItem() {

        Item item1 = new Item(1L, "item1", "description Item1", true, 1L, null);
        User user1 = new User(1L, "userName", "user@mail.ru");
        when(itemRepository.getById(any())).thenReturn(item1);
        ItemDto itemDto = ItemMapper.toItemDto(item1);
        ItemDto itemDto1After = itemService.update(itemDto, item1.getId(), user1.getId());
        assertEquals(itemDto1After.getId(), itemDto.getId());
        assertEquals(itemDto1After.getName(), itemDto.getName());
        assertEquals(itemDto1After.getDescription(), itemDto.getDescription());
    }

    @Test
    void shouldGetAllByUserId() {
        Item item1 = new Item(1L, "item1", "description Item1", true, 1L, null);
        when(itemRepository.findAllByOwnerOrderByIdAsc(anyLong(), any())).thenReturn(new PageImpl<Item>(Collections.singletonList(item1)));


        itemService.getAllByUserId(1L, 0, 1);
        verify(itemRepository).findAllByOwnerOrderByIdAsc(anyLong(), any());
        verify(commentRepository).findByItemIn(any(), any());
        verify(bookingRepository).findFirstByItemInAndAndStartLessThanEqualAndStatusEqualsOrderByStartDesc(any(), any(), any());
        verify(bookingRepository).findFirstByItemInAndAndStartAfterAndStatusEqualsOrderByStartAsc(any(), any(), any());
    }

    @Test
    void shouldGetById() {
        Item item1 = new Item(1L, "item1", "description Item1", true, 1L, null);
        User user1 = new User(1L, "userName", "user@mail.ru");
        Booking bookingNext = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item1, user1, BookingStatus.WAITING);
        Booking bookingLast = new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item1, user1, BookingStatus.WAITING);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(bookingRepository.findFirstByItemAndStatusLikeAndStartAfterOrderByStartAsc(any(), any(), any())).thenReturn(Optional.of(bookingNext));
        when(bookingRepository.findFirstByItemAndStatusLikeAndStartLessThanEqualOrderByStartDesc(any(), any(), any())).thenReturn(Optional.of(bookingLast));

        itemService.getById(1, 1);

        verify(itemRepository).findById(anyLong());
        verify(bookingRepository).findFirstByItemAndStatusLikeAndStartAfterOrderByStartAsc(any(), any(), any());
        verify(bookingRepository).findFirstByItemAndStatusLikeAndStartLessThanEqualOrderByStartDesc(any(), any(), any());

    }

    @Test
    void shouldSearch() {
        Item item1 = new Item(1L, "item1", "description Item1", true, 1L, null);
        ItemDto itemDto1 = ItemMapper.toItemDto(item1);
        List<ItemDto> itemDtosBefore = List.of(itemDto1);
        when(itemRepository.search(any(), any()))
                .thenReturn(new PageImpl<Item>(Collections.singletonList(item1)));

        List<ItemDto> itemDtosAfter = itemService.search("text", 0, 1);

        assertEquals(itemDtosBefore.size(), itemDtosAfter.size());
        assertEquals(itemDtosBefore.get(0).getId(), itemDtosAfter.get(0).getId());
        assertEquals(itemDtosBefore.get(0).getName(), itemDtosAfter.get(0).getName());
        assertEquals(itemDtosBefore.get(0).getDescription(), itemDtosAfter.get(0).getDescription());
    }

    @Test
    void shouldCreateComment() {
        Item item1 = new Item(1L, "item1", "description Item1", true, 1L, null);
        User user1 = new User(1L, "userName", "user@mail.ru");
        Booking bookingLast = new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item1, user1, BookingStatus.APPROVED);
        Comment comment1 = new Comment(1L, "text comment1", user1, item1, LocalDateTime.now());
        CommentDtoInput commentDto1 = CommentMapper.toCommentDtoInput(comment1);
        CommentDtoOutput commentDtoOutput = CommentMapper.toCommentDtoOutput(comment1);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(bookingRepository.findLastBookings(anyLong(), anyLong(), any())).thenReturn(List.of(bookingLast));
        when(commentRepository.save(any())).thenReturn(comment1);

        CommentDtoOutput commentDtoOutputAfter = itemService.createComment(commentDto1, 1L, 1L);
        assertEquals(commentDtoOutput.getId(), commentDtoOutputAfter.getId());
        assertEquals(commentDtoOutput.getText(), commentDtoOutputAfter.getText());
        assertEquals(commentDtoOutput.getAuthorName(), commentDtoOutputAfter.getAuthorName());
        assertEquals(commentDtoOutput.getCreated(), commentDtoOutputAfter.getCreated());
    }
}
