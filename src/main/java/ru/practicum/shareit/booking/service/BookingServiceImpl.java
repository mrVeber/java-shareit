package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import org.apache.commons.lang3.EnumUtils;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private static final String ALL = "ALL";

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingDto create(long userId, BookingRequestDto createBookingDto) {
        checkIfUserExists(userId);
        checkIfItemExists(createBookingDto.getItemId());
        checkIfItemAvailable(createBookingDto.getItemId());
        checkCorrectDateTimePeriod(createBookingDto.getStart(), createBookingDto.getEnd());
        User booker = userRepository.getReferenceById(userId);
        Item item = itemRepository.getReferenceById(createBookingDto.getItemId());
        checkUserOwnItem(item.getOwnerId(), booker.getId());
        Booking booking = BookingMapper.fromBookingRequestDto(createBookingDto);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);
        Booking createdBooking = bookingRepository.save(booking);
        return BookingMapper.toStandardBookingDto(createdBooking);
    }

    @Transactional
    @Override
    public BookingDto update(long ownerId, long bookingId, BookingStatus status) {
        checkIfUserExists(ownerId);
        checkIfBookingExists(bookingId);
        Booking booking = bookingRepository.getReferenceById(bookingId);
        checkCorrectItemOwner(ownerId, booking.getItem().getOwnerId());
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BookingBadRequestException(String.format("Статус бронирования %d уже подтверждён.", bookingId));
        }
        booking.setStatus(status);
        return BookingMapper.toStandardBookingDto(booking);
    }

    @Override
    public BookingDto get(long userId, long bookingId) {
        checkIfUserExists(userId);
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            if (userId == booking.getBooker().getId() || userId == booking.getItem().getOwnerId()) {
                return BookingMapper.toStandardBookingDto(booking);
            }
        }
        throw new BookingNotFoundException(String.format("Бронирования с идентификатором %d для пользователя %d не найдено.", bookingId, userId));
    }

    @Override
    public List<BookingDto> getAllByBooker(long bookerId, String status) {
        checkIfUserExists(bookerId);
        if (EnumUtils.isValidEnum(BookingStatus.class, status)) {
            BookingStatus bookingStatus = EnumUtils.getEnum(BookingStatus.class, status);
            return findAllByBookerAndStatus(bookerId, bookingStatus);
        } else {
            throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingDto> getAllByOwner(long ownerId, String status) {
        checkIfUserExists(ownerId);
        if (EnumUtils.isValidEnum(BookingStatus.class, status)) {
            BookingStatus bookingStatus = EnumUtils.getEnum(BookingStatus.class, status);
            return findAllByOwnerAndStatus(ownerId, bookingStatus);
        } else {
            throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private void checkIfUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не найден.", userId));
        }
    }

    private void checkIfItemExists(long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ItemNotFoundException(String.format("Вещь с идентификатором %d не найдена.", itemId));
        }
    }

    private void checkIfItemAvailable(long itemId) {
        if (!itemRepository.getReferenceById(itemId).getAvailable()) {
            throw new ItemBadRequestException(String.format("Вещь с идентификатором %d не доступна.", itemId));
        }
    }

    private void checkCorrectDateTimePeriod(LocalDateTime start, LocalDateTime end) {
        if (start.equals(end)) {
            throw new BookingBadRequestException("Дата начала бронирования не может быть равна дате окончания.");
        }
        if (start.isAfter(end)) {
            throw new BookingBadRequestException("Дата начала бронирования указана позже даты окончания.");
        }
    }

    private void checkIfBookingExists(long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new BookingNotFoundException(String.format("Бронирование с идентификатором %d не найдено.", bookingId));
        }
    }

    private void checkCorrectItemOwner(long expectedOwnerId, long actualOwnerId) {
        if (expectedOwnerId != actualOwnerId) {
            throw new BookingNotFoundException("Неверно указан пользователь вещи.");
        }
    }

    private void checkUserOwnItem(long ownerId, long bookerId) {
        if (ownerId == bookerId) {
            throw new ItemNotFoundException("Невозможно завести бронь на свою же вещь.");
        }
    }

    private List<BookingDto> findAllByBookerAndStatus(long bookerId, BookingStatus status) {
        List<Booking> bookings;
        switch (status) {
            case ALL:
                bookings = bookingRepository.findAllByBooker(bookerId, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case PAST:
                bookings = bookingRepository.findAllPastByBooker(bookerId, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case CURRENT:
                bookings = bookingRepository.findAllCurrentByBooker(bookerId, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findAllFutureByBooker(bookerId, Sort.by(Sort.Direction.DESC, "start"));
                break;
            default:
                bookings = bookingRepository.findAllByBookerAndStatus(bookerId, status, Sort.by(Sort.Direction.DESC, "start"));
                break;
        }
        return bookings.stream().map(BookingMapper::toStandardBookingDto).collect(Collectors.toList());
    }

    private List<BookingDto> findAllByOwnerAndStatus(long ownerId, BookingStatus status) {
        List<Booking> bookings;
        switch (status) {
            case ALL:
                bookings = bookingRepository.findAllByOwner(ownerId, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case PAST:
                bookings = bookingRepository.findAllPastByOwner(ownerId, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case CURRENT:
                bookings = bookingRepository.findAllCurrentByOwner(ownerId, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findAllFutureByOwner(ownerId, Sort.by(Sort.Direction.DESC, "start"));
                break;
            default:
                bookings = bookingRepository.findAllByOwnerAndStatus(ownerId, status, Sort.by(Sort.Direction.DESC, "start"));
                break;
        }
        return bookings.stream().map(BookingMapper::toStandardBookingDto).collect(Collectors.toList());
    }
}