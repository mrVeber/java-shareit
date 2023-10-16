package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingState;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private static final Sort SORT_START_DESC = Sort.by(Sort.Direction.DESC, "start");
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDtoFullResponse create(long userId, BookingDtoCreate bookingDtoCreate) {

        Item item = itemRepository.findById(bookingDtoCreate.getItemId()).orElseThrow(() -> {
            throw new NotFoundException("Item with id = " + bookingDtoCreate.getItemId() + " not found");
        });

        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("User with id = " + userId + " not found");
        });

        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);

        if (userId == booking.getItem().getOwner().getId()) {
            throw new NotFoundException("Unable to book your own");
        }

        if (Boolean.FALSE.equals(booking.getItem().getAvailable())) {
            throw new BadRequestException("Item not available for booking now");
        }

        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toBookingRS(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoFullResponse update(long bookingId, long ownerId, boolean approved) {

        userRepository.findById(ownerId).orElseThrow(() -> {
            throw new NotFoundException("User with id = " + ownerId + " not found");
        });

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException("Booking with id = " + bookingId + " not found");
        });

        if (booking.getStatus().equals(BookingStatus.APPROVED)
                || booking.getStatus().equals(BookingStatus.REJECTED)) {
            throw new BadRequestException("This booking can't changed status");
        }

        if (ownerId == booking.getItem().getOwner().getId()) {
            if (approved) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
            return BookingMapper.toBookingRS(bookingRepository.save(booking));
        } else {
            throw new NotFoundException("This user can't make this");
        }

    }

    @Override
    @Transactional(readOnly = true)
    public BookingDtoFullResponse get(long bookingId, long ownerId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()
                -> new NotFoundException("Booking with id " + bookingId + " was not found"));

        if (ownerId == booking.getItem().getOwner().getId()
                || ownerId == booking.getBooker().getId()) {
            return BookingMapper.toBookingRS(booking);
        } else {
            throw new NotFoundException("User with id " + ownerId + " can't see this information");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoFullResponse> getBookings(long ownerId, String state, PageRequest pageRequest) {

        validStateAndUser(ownerId, state);

        List<Booking> bookings = List.of();

        switch (BookingState.valueOf(state)) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(ownerId, pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerCurrent(
                        ownerId,
                        LocalDateTime.now(),
                        SORT_START_DESC,
                        pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerPast(
                        ownerId,
                        LocalDateTime.now(),
                        SORT_START_DESC,
                        pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerFuture(
                        ownerId,
                        LocalDateTime.now(),
                        SORT_START_DESC,
                        pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerAndStatus(
                        ownerId,
                        BookingStatus.WAITING,
                        SORT_START_DESC,
                        pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerAndStatus(
                        ownerId,
                        BookingStatus.REJECTED,
                        SORT_START_DESC,
                        pageRequest);
                break;
        }
        return bookings.stream()
                .map(BookingMapper::toBookingRS)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoFullResponse> getBookingFromOwner(long ownerId, String state, PageRequest pageRequest) {

        validStateAndUser(ownerId, state);

        List<Booking> bookings = List.of();

        switch (BookingState.valueOf(state)) {
            case ALL:
                bookings = bookingRepository.findByItemOwnerId(
                        ownerId,
                        SORT_START_DESC,
                        pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerCurrent(
                        ownerId,
                        LocalDateTime.now(),
                        SORT_START_DESC,
                        pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerPast(
                        ownerId,
                        LocalDateTime.now(),
                        SORT_START_DESC,
                        pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerFuture(
                        ownerId,
                        LocalDateTime.now(),
                        SORT_START_DESC,
                        pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerAndStatus(
                        ownerId,
                        BookingStatus.WAITING,
                        SORT_START_DESC,
                        pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerAndStatus(
                        ownerId,
                        BookingStatus.REJECTED,
                        SORT_START_DESC,
                        pageRequest);
                break;
        }
        return bookings.stream()
                .map(BookingMapper::toBookingRS)
                .collect(Collectors.toList());
    }

    private void validStateAndUser(long ownerId, String state) {
        BookingState[] states = BookingState.values();

        if (Arrays.stream(states).noneMatch(x -> x.name().equals(state))) {
            throw new BadRequestException("Unknown state: " + state);
        }

        userRepository.findById(ownerId).orElseThrow(() -> {
            throw new NotFoundException("User with id = " + ownerId + " not found");
        });
    }
}
