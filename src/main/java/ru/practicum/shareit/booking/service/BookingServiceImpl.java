package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBooking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.BookingException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ValidatorBookingService validator;

    private static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");

    @Transactional
    public BookingDto createBooking(NewBooking newBooking, Long bookerId) {
        Item item = validator.validateItemBooking(newBooking.getItemId());
        User user = validator.getBookingUser(bookerId);
        Booking booking = bookingMapper.fromDtoNewToModel(newBooking);
        validator.validBookerAsOwner(bookerId, item);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        return bookingMapper.fromBookingToDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingDto approveOrRejected(Long bookingId, boolean approved, long userId) {
        validator.getBookingUser(userId);
        Booking booking = validator.getBooking(bookingId);
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BookingException(String.format("У бронирования с id %d уже стоит статус %s",
                    bookingId, BookingStatus.APPROVED.name()));
        }
        if (booking.getItem().getOwner() != userId) {
            throw new NotFoundException("Пользователь не владелец бронируемой вещи");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingMapper.fromBookingToDto(booking);
    }

    public BookingDto getBookingById(Long bookingId, Long userID) {
        validator.getBookingUser(userID);
        Booking booking = validator.getBooking(bookingId);
        if (!Objects.equals(booking.getBooker().getId(), userID)
                && !Objects.equals(booking.getItem().getOwner(), userID)) {
            throw new NotFoundException("Запрос может быть выполнен либо автором бронирования, либо владельцем вещи");
        }
        return bookingMapper.fromBookingToDto(booking);
    }

    public List<BookingDto> getAllBookingByUserId(Long userId, String stateStr) {
        validator.getBookingUser(userId);
        BookingState state = BookingState.checkAndConvert(stateStr.toUpperCase());
        List<Booking> bookings;
        switch (state) {
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId,
                        BookingStatus.WAITING, SORT_BY_START_DESC);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId,
                        BookingStatus.REJECTED, SORT_BY_START_DESC);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBefore(userId,
                        LocalDateTime.now(), SORT_BY_START_DESC);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfter(userId,
                        LocalDateTime.now(), SORT_BY_START_DESC);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterAndEndBefore(userId,
                        LocalDateTime.now(), SORT_BY_START_DESC);
                break;
            default:
                bookings = bookingRepository.findAllByBookerId(userId, SORT_BY_START_DESC);
        }

        return bookings
                .stream()
                .map(bookingMapper::fromBookingToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsOfOwner(Long ownerId, String stateStr) {
        validator.getBookingUser(ownerId);
        BookingState state = BookingState.checkAndConvert(stateStr.toUpperCase());

        List<Booking> bookings;
        switch (state) {
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerAndStatus(ownerId,
                        BookingStatus.WAITING, SORT_BY_START_DESC);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerAndStatus(ownerId,
                        BookingStatus.REJECTED, SORT_BY_START_DESC);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerAndEndBefore(ownerId,
                        LocalDateTime.now(), SORT_BY_START_DESC);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerAndStartAfter(ownerId,
                        LocalDateTime.now(), SORT_BY_START_DESC);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(ownerId,
                        LocalDateTime.now(), SORT_BY_START_DESC);
                break;
            default:
                bookings = bookingRepository.findAllByItemOwner(ownerId, SORT_BY_START_DESC);
        }
        return bookings.stream()
                .map(bookingMapper::fromBookingToDto)
                .collect(Collectors.toList());
    }
}