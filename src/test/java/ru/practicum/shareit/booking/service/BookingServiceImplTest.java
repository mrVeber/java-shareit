package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private final long bookerId = 10L;
    private final String bookerName = "bookerUser";
    private final String bookerMail = "booker@mail.com";
    private final User booker = User.builder().id(bookerId).name(bookerName).email(bookerMail).build();
    private final UserDtoResponse responseBookerDto = UserDtoResponse.builder().id(bookerId).name(bookerName)
            .email(bookerMail).build();

    private final long notExistingBookerId = 9999L;

    private final long ownerId = 11L;
    private final String ownerName = "ownerUser";
    private final String ownerMail = "owner@mail.com";
    private final User owner = User.builder().id(ownerId).name(ownerName).email(ownerMail).build();

    private final long itemId = 5L;
    private final String itemName = "itemTest";
    private final String itemDescription = "itemTestTestTest";
    private final Item item = Item.builder().id(itemId).name(itemName).description(itemDescription).available(true)
            .owner(owner).build();
    private final ItemDtoResponse responseItemDto = ItemDtoResponse.builder().id(itemId).name(itemName)
            .description(itemDescription).available(true).requestId(0).build();

    private final long notExistingItemId = 99999L;

    private final long bookingId = 300L;
    private final LocalDateTime bookingStartOne = LocalDateTime.of(2023, 9, 11, 12, 0);
    private final LocalDateTime bookingEndOne = LocalDateTime.of(2023, 9, 12, 12, 0);
    private final BookingDtoRequest requestBookingDto = BookingDtoRequest.builder().start(bookingStartOne)
            .end(bookingEndOne).itemId(itemId).build();
    private final Booking booking = Booking.builder().id(bookingId).start(bookingStartOne).end(bookingEndOne)
            .status(BookingStatus.WAITING).item(item).booker(booker).build();
    private final Booking bookingApproved = Booking.builder().id(bookingId).start(bookingStartOne).end(bookingEndOne)
            .status(BookingStatus.APPROVED).item(item).booker(booker).build();
    private final Booking bookingNotApproved = Booking.builder().id(bookingId).start(bookingStartOne).end(bookingEndOne)
            .status(BookingStatus.REJECTED).item(item).booker(booker).build();
    private final BookingDtoResponse responseBookingDto = BookingDtoResponse.builder().id(bookingId)
            .start(bookingStartOne).end(bookingEndOne).status(BookingStatus.WAITING).item(responseItemDto)
            .booker(responseBookerDto).build();
    private final BookingDtoResponse responseBookingApprovedDto = BookingDtoResponse.builder().id(bookingId)
            .start(bookingStartOne).end(bookingEndOne).status(BookingStatus.APPROVED).item(responseItemDto)
            .booker(responseBookerDto).build();
    private final BookingDtoResponse responseBookingNotApprovedDto = BookingDtoResponse.builder().id(bookingId)
            .start(bookingStartOne).end(bookingEndOne).status(BookingStatus.REJECTED).item(responseItemDto)
            .booker(responseBookerDto).build();

    private final long notExistingBookingId = 999L;

    private Page<Booking> getBookingPage(Booking booking) {
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        Page<Booking> bookings = new PageImpl<>(bookingList);
        return bookings;
    }

    @Test
    void createBookingTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(booker));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito.when(bookingRepository.save(Mockito.any()))
                .thenReturn(booking);

        assertEquals(bookingService.create(requestBookingDto, bookerId), responseBookingDto);
    }

    @Test
    void createBookingNotExistingUserTest() {
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenThrow(new NotFoundException(String.format("Пользователь с id = %s отсутствует в БД. " +
                        "Выполнить операцию невозможно!", notExistingBookerId)));
        Exception exception = assertThrows(NotFoundException.class, () -> bookingService.create(requestBookingDto,
                notExistingBookerId));

        assertEquals("Пользователь с id = " + notExistingBookerId + " отсутствует в БД. Выполнить операцию невозможно!",
                exception.getMessage());
    }

    @Test
    void createBookingNotExistingItemTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(booker));
        Mockito.when(itemRepository.findById(Mockito.any()))
                .thenThrow(new NotFoundException(String.format("Вещь с id = %s отсутствует в БД. " +
                        "Выполнить операцию невозможно!", notExistingItemId)));
        Exception exception = assertThrows(NotFoundException.class, () -> bookingService.create(requestBookingDto,
                notExistingItemId));

        assertEquals("Вещь с id = " + notExistingItemId + " отсутствует в БД. Выполнить операцию невозможно!",
                exception.getMessage());
    }



    @Test
    void approveBookingTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        Mockito.when(bookingRepository.save(Mockito.any()))
                .thenReturn(bookingApproved);
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(booker));

        assertEquals(bookingService.approve(bookingId, true, ownerId), responseBookingApprovedDto);
    }

    @Test
    void notApproveBookingTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        Mockito.when(bookingRepository.save(Mockito.any()))
                .thenReturn(bookingNotApproved);
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(booker));

        assertEquals(bookingService.approve(bookingId, false, ownerId), responseBookingNotApprovedDto);
    }

    @Test
    void getBookingByIdTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(bookingApproved));
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(booker));

        assertEquals(bookingService.getById(bookingId, ownerId), responseBookingApprovedDto);
    }

    @Test
    void getNotExistingBookingByIdTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        Mockito.when(bookingRepository.findById(Mockito.any()))
                .thenThrow(new NotFoundException(String.format("Бронирование с id = %s отсутствует в БД. " +
                        "Выполнить операцию невозможно!", notExistingBookingId)));
        Exception exception = assertThrows(NotFoundException.class, () -> bookingService.getById(notExistingBookingId,
                ownerId));

        assertEquals("Бронирование с id = " + notExistingBookingId + " отсутствует в БД. Выполнить операцию невозможно!",
                exception.getMessage());
    }

    @Test
    void getBookingsOneBookerTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(booker));
        Mockito.when(bookingRepository.findAllByBookerId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(getBookingPage(bookingApproved));

        assertEquals(bookingService.getBookingsOneBooker(bookerId, "ALL", 0, 10),
                List.of(responseBookingApprovedDto));
    }

    @Test
    void getBookingsOneBookerEmptyTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(booker));
        List<Booking> bookingList = new ArrayList<>();
        Page<Booking> bookings = new PageImpl<>(bookingList);
        Mockito.when(bookingRepository.findAllByBookerId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(bookings);

        assertEquals(bookingService.getBookingsOneBooker(bookerId, "ALL", 0, 10),
                new ArrayList<>());
    }

    @Test
    void getBookingsOneBookerStateCurrentTest() {
        LocalDateTime currentBookingStart = LocalDateTime.of(2023, 9, 1, 12, 0);
        LocalDateTime currentBookingEnd = LocalDateTime.of(2023, 9, 30, 12, 0);
        bookingApproved.setStart(currentBookingStart);
        bookingApproved.setEnd(currentBookingEnd);
        responseBookingApprovedDto.setStart(currentBookingStart);
        responseBookingApprovedDto.setEnd(currentBookingEnd);

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(booker));
        Mockito.when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(Mockito.anyLong(), Mockito.any(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(getBookingPage(bookingApproved));

        assertEquals(bookingService.getBookingsOneBooker(bookerId, "CURRENT", 0, 10),
                List.of(responseBookingApprovedDto));
    }

    @Test
    void getBookingsOneBookerStatePastTest() {
        LocalDateTime currentBookingStart = LocalDateTime.of(2023, 8, 1, 12, 0);
        LocalDateTime currentBookingEnd = LocalDateTime.of(2023, 8, 31, 12, 0);
        bookingApproved.setStart(currentBookingStart);
        bookingApproved.setEnd(currentBookingEnd);
        responseBookingApprovedDto.setStart(currentBookingStart);
        responseBookingApprovedDto.setEnd(currentBookingEnd);

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(booker));
        Mockito.when(bookingRepository.findAllByBookerIdAndEndBefore(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(getBookingPage(bookingApproved));

        assertEquals(bookingService.getBookingsOneBooker(bookerId, "PAST", 0, 10),
                List.of(responseBookingApprovedDto));
    }

    @Test
    void getBookingsOneBookerStateFutureTest() {
        LocalDateTime currentBookingStart = LocalDateTime.of(2023, 10, 1, 12, 0);
        LocalDateTime currentBookingEnd = LocalDateTime.of(2023, 10, 31, 12, 0);
        bookingApproved.setStart(currentBookingStart);
        bookingApproved.setEnd(currentBookingEnd);
        responseBookingApprovedDto.setStart(currentBookingStart);
        responseBookingApprovedDto.setEnd(currentBookingEnd);

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(booker));
        Mockito.when(bookingRepository.findAllByBookerIdAndStartAfter(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(getBookingPage(bookingApproved));

        assertEquals(bookingService.getBookingsOneBooker(bookerId, "FUTURE", 0, 10),
                List.of(responseBookingApprovedDto));
    }

    @Test
    void getBookingsOneBookerStateWaitingTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(booker));
        Mockito.when(bookingRepository.findAllByBookerIdAndStatus(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(getBookingPage(booking));

        assertEquals(bookingService.getBookingsOneBooker(bookerId, "WAITING", 0, 10),
                List.of(responseBookingDto));
    }

    @Test
    void getBookingsOneBookerStateRejectedTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(booker));
        Mockito.when(bookingRepository.findAllByBookerIdAndStatus(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(getBookingPage(bookingNotApproved));

        assertEquals(bookingService.getBookingsOneBooker(bookerId, "REJECTED", 0, 10),
                List.of(responseBookingNotApprovedDto));
    }

    @Test
    void getBookingsOneOwnerTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        Mockito.when(itemRepository.existsAllByOwnerId(Mockito.anyLong()))
                .thenReturn(true);
        Mockito.when(bookingRepository.allBookersByOwnerId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(getBookingPage(bookingApproved));

        assertEquals(bookingService.getBookingsOneOwner(bookerId, "ALL", 0, 10),
                List.of(responseBookingApprovedDto));
    }

    @Test
    void getBookingsOneOwnerStateCurrentTest() {
        LocalDateTime currentBookingStart = LocalDateTime.of(2023, 9, 1, 12, 0);
        LocalDateTime currentBookingEnd = LocalDateTime.of(2023, 9, 30, 12, 0);
        bookingApproved.setStart(currentBookingStart);
        bookingApproved.setEnd(currentBookingEnd);
        responseBookingApprovedDto.setStart(currentBookingStart);
        responseBookingApprovedDto.setEnd(currentBookingEnd);

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        Mockito.when(itemRepository.existsAllByOwnerId(Mockito.anyLong()))
                .thenReturn(true);
        Mockito.when(bookingRepository.currentBookersByOwnerId(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(getBookingPage(bookingApproved));

        assertEquals(bookingService.getBookingsOneOwner(bookerId, "CURRENT", 0, 10),
                List.of(responseBookingApprovedDto));
    }

    @Test
    void getBookingsOneOwnerStatePastTest() {
        LocalDateTime currentBookingStart = LocalDateTime.of(2023, 8, 1, 12, 0);
        LocalDateTime currentBookingEnd = LocalDateTime.of(2023, 8, 31, 12, 0);
        bookingApproved.setStart(currentBookingStart);
        bookingApproved.setEnd(currentBookingEnd);
        responseBookingApprovedDto.setStart(currentBookingStart);
        responseBookingApprovedDto.setEnd(currentBookingEnd);

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        Mockito.when(itemRepository.existsAllByOwnerId(Mockito.anyLong()))
                .thenReturn(true);
        Mockito.when(bookingRepository.pastBookersByOwnerId(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(getBookingPage(bookingApproved));

        assertEquals(bookingService.getBookingsOneOwner(bookerId, "PAST", 0, 10),
                List.of(responseBookingApprovedDto));
    }

    @Test
    void getBookingsOneOwnerStateFutureTest() {
        LocalDateTime currentBookingStart = LocalDateTime.of(2023, 10, 1, 12, 0);
        LocalDateTime currentBookingEnd = LocalDateTime.of(2023, 10, 31, 12, 0);
        bookingApproved.setStart(currentBookingStart);
        bookingApproved.setEnd(currentBookingEnd);
        responseBookingApprovedDto.setStart(currentBookingStart);
        responseBookingApprovedDto.setEnd(currentBookingEnd);

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        Mockito.when(itemRepository.existsAllByOwnerId(Mockito.anyLong()))
                .thenReturn(true);
        Mockito.when(bookingRepository.futureBookersByOwnerId(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(getBookingPage(bookingApproved));

        assertEquals(bookingService.getBookingsOneOwner(bookerId, "FUTURE", 0, 10),
                List.of(responseBookingApprovedDto));
    }

    @Test
    void getBookingsOneOwnerStateWaitingTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        Mockito.when(itemRepository.existsAllByOwnerId(Mockito.anyLong()))
                .thenReturn(true);
        Mockito.when(bookingRepository.bookersByStatusAndOwnerId(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(getBookingPage(booking));

        assertEquals(bookingService.getBookingsOneOwner(bookerId, "WAITING", 0, 10),
                List.of(responseBookingDto));
    }

    @Test
    void getBookingsOneOwnerStateRejectedTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(owner));
        Mockito.when(itemRepository.existsAllByOwnerId(Mockito.anyLong()))
                .thenReturn(true);
        Mockito.when(bookingRepository.bookersByStatusAndOwnerId(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(getBookingPage(bookingNotApproved));

        assertEquals(bookingService.getBookingsOneOwner(bookerId, "REJECTED", 0, 10),
                List.of(responseBookingNotApprovedDto));
    }
}
