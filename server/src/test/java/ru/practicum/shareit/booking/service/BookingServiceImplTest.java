package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingDtoFullResponse;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRepository itemRepository;
    @InjectMocks
    BookingServiceImpl bookingService;

    BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(
            1L,
            LocalDateTime.now().minusHours(2),
            LocalDateTime.now().minusHours(1),
            1L
    );
    User user = new User(
            1L,
            "name",
            "email@email.ru");
    User user2 = new User(
            2L,
            "name",
            "email@email.ru");
    Item item = new Item(
            1L,
            "name",
            "description",
            true,
            user,
            null);

    @Test
    void create_whenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> bookingService.create(1L, bookingDtoCreate));
        assertEquals("Item with id = 1 not found", ex.getMessage());
    }

    @Test
    void create_whenUserNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> bookingService.create(1L, bookingDtoCreate));
        assertEquals("User with id = 1 not found", ex.getMessage());
    }

    @Test
    void create_whenOwnerTryingToBookHisItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user2));

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> bookingService.create(1L, bookingDtoCreate));
        assertEquals("Unable to book your own", ex.getMessage());
    }

    @Test
    void create_whenItemNotAvailable() {
        Item itemTest = item;
        itemTest.setId(49L);
        itemTest.setAvailable(false);
        bookingDtoCreate.setItemId(49L);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user2));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> bookingService.create(4L, bookingDtoCreate));
        assertEquals("Item not available for booking now", ex.getMessage());
    }

    @Test
    void changeStatus_whenBookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> bookingService.update(1L, 1L, true));
        assertEquals("Booking with id = 1 not found", ex.getMessage());
    }

    @Test
    void changeStatus_whenBooking_REJECTED() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        booking.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> bookingService.update(1L, 1L, true));
        assertEquals("This booking can't changed status", ex.getMessage());
    }

    @Test
    void changeStatus_whenBooking_APPROVED() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> bookingService.update(1L, 1L, true));
        assertEquals("This booking can't changed status", ex.getMessage());
    }

    @Test
    void getBookingInfo_whenOwner_thenReturnInfo() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDtoFullResponse resp = bookingService.get(user.getId(), booking.getId());
        assertNotNull(resp);
        assertEquals(booking.getItem().getName(), resp.getItem().getName());
    }

    @Test
    void getBookingInfo_whenNotOwner() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> bookingService.get(booking.getId(), 999L));
        assertEquals("User with id 999 can't see this information", ex.getMessage());
    }

    @Test
    void getBookingInfo_whenBookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> bookingService.get(1L, 1L));
        assertEquals("Booking with id 1 was not found", ex.getMessage());
    }

    @Test
    void getByBooker_whenBookerAllState() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any())).thenReturn(Collections.singletonList(booking));

        List<BookingDtoFullResponse> resp = bookingService.getBookings(user.getId(), "ALL", PageRequest.ofSize(1));
        assertFalse(resp.isEmpty());
        assertEquals(booking.getItem().getName(), resp.get(0).getItem().getName());
        verify(bookingRepository, times(1)).findAllByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getByBooker_whenBookerCurrentState() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerCurrent(anyLong(), any(), any(), any())).thenReturn(Collections.singletonList(booking));

        List<BookingDtoFullResponse> resp = bookingService.getBookings(user.getId(), "CURRENT", PageRequest.ofSize(1));
        assertFalse(resp.isEmpty());
        assertEquals(booking.getItem().getName(), resp.get(0).getItem().getName());
        verify(bookingRepository, times(1)).findByBookerCurrent(anyLong(), any(), any(), any());
    }

    @Test
    void getByBooker_whenBookerPastState() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerPast(anyLong(), any(), any(), any())).thenReturn(Collections.singletonList(booking));

        List<BookingDtoFullResponse> resp = bookingService.getBookings(user.getId(), "PAST", PageRequest.ofSize(1));
        assertFalse(resp.isEmpty());
        assertEquals(booking.getItem().getName(), resp.get(0).getItem().getName());
        verify(bookingRepository, times(1)).findByBookerPast(anyLong(), any(), any(), any());
    }

    @Test
    void getByBooker_whenBookerFutureState() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerFuture(anyLong(), any(), any(), any())).thenReturn(Collections.singletonList(booking));

        List<BookingDtoFullResponse> resp = bookingService.getBookings(user.getId(), "FUTURE", PageRequest.ofSize(1));
        assertFalse(resp.isEmpty());
        assertEquals(booking.getItem().getName(), resp.get(0).getItem().getName());
        verify(bookingRepository, times(1)).findByBookerFuture(anyLong(), any(), any(), any());
    }

    @Test
    void getByBooker_whenBookerWaitingStatus() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerAndStatus(anyLong(), any(), any(), any())).thenReturn(Collections.singletonList(booking));

        List<BookingDtoFullResponse> resp = bookingService.getBookings(user.getId(), "WAITING", PageRequest.ofSize(1));
        assertFalse(resp.isEmpty());
        assertEquals(booking.getItem().getName(), resp.get(0).getItem().getName());
        verify(bookingRepository, times(1)).findByBookerAndStatus(anyLong(), any(), any(), any());
    }

    @Test
    void getByBooker_whenBookerRejectedStatus() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerAndStatus(anyLong(), any(), any(), any())).thenReturn(Collections.singletonList(booking));

        List<BookingDtoFullResponse> resp = bookingService.getBookings(user.getId(), "REJECTED", PageRequest.ofSize(1));
        assertFalse(resp.isEmpty());
        assertEquals(booking.getItem().getName(), resp.get(0).getItem().getName());
        verify(bookingRepository, times(1)).findByBookerAndStatus(anyLong(), any(), any(), any());
    }

    @Test
    void getByOwner_whenBookerCurrentState() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItemOwnerCurrent(anyLong(), any(), any(), any())).thenReturn(Collections.singletonList(booking));

        List<BookingDtoFullResponse> resp = bookingService.getBookingFromOwner(user.getId(), "CURRENT", PageRequest.ofSize(1));
        assertFalse(resp.isEmpty());
        assertEquals(booking.getItem().getName(), resp.get(0).getItem().getName());
        verify(bookingRepository, times(1)).findByItemOwnerCurrent(anyLong(), any(), any(), any());
    }

    @Test
    void getByOwner_whenBookerPastState() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItemOwnerPast(anyLong(), any(), any(), any())).thenReturn(Collections.singletonList(booking));

        List<BookingDtoFullResponse> resp = bookingService.getBookingFromOwner(user.getId(), "PAST", PageRequest.ofSize(1));
        assertFalse(resp.isEmpty());
        assertEquals(booking.getItem().getName(), resp.get(0).getItem().getName());
        verify(bookingRepository, times(1)).findByItemOwnerPast(anyLong(), any(), any(), any());
    }

    @Test
    void getByOwner_whenBookerFutureState() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItemOwnerFuture(anyLong(), any(), any(), any())).thenReturn(Collections.singletonList(booking));

        List<BookingDtoFullResponse> resp = bookingService.getBookingFromOwner(user.getId(), "FUTURE", PageRequest.ofSize(1));
        assertFalse(resp.isEmpty());
        assertEquals(booking.getItem().getName(), resp.get(0).getItem().getName());
        verify(bookingRepository, times(1)).findByItemOwnerFuture(anyLong(), any(), any(), any());
    }

    @Test
    void getByOwner_whenBookerWaitingStatus() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItemOwnerAndStatus(anyLong(), any(), any(), any())).thenReturn(Collections.singletonList(booking));

        List<BookingDtoFullResponse> resp = bookingService.getBookingFromOwner(user.getId(), "WAITING", PageRequest.ofSize(1));
        assertFalse(resp.isEmpty());
        assertEquals(booking.getItem().getName(), resp.get(0).getItem().getName());
        verify(bookingRepository, times(1)).findByItemOwnerAndStatus(anyLong(), any(), any(), any());
    }

    @Test
    void getByOwner_whenBookerRejectedStatus() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItemOwnerAndStatus(anyLong(), any(), any(), any())).thenReturn(Collections.singletonList(booking));

        List<BookingDtoFullResponse> resp = bookingService.getBookingFromOwner(user.getId(), "REJECTED", PageRequest.ofSize(1));
        assertFalse(resp.isEmpty());
        assertEquals(booking.getItem().getName(), resp.get(0).getItem().getName());
        verify(bookingRepository, times(1)).findByItemOwnerAndStatus(anyLong(), any(), any(), any());
    }

    @Test
    void getByOwner_whenBookerNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingFromOwner(user.getId(), "ALL", PageRequest.ofSize(1)));
        assertEquals("User with id = 1 not found", ex.getMessage());
    }

    @Test
    void getByOwner_whenUnsupportedStatus() {
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> bookingService.getBookingFromOwner(user.getId(), "unsupported", PageRequest.ofSize(1)));
        assertEquals("Unknown state: unsupported", ex.getMessage());
    }

    @Test
    void create_whenAllIsOk_thenBookingSaved() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        BookingMapper.toBookingDtoResponse(booking);
        BookingDtoFullResponse bDto = BookingMapper.toBookingRS(booking);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user2));
        when(bookingRepository.save(any())).thenReturn(booking);
        bookingService.create(2L, bookingDtoCreate);
        assertEquals(bDto.getId(), bookingDtoCreate.getId());
        verify(bookingRepository).save(any());
    }
}
