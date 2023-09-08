package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public OutputBookingDto create(InputBookingDto bookingDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("item not found"));
        if (item.getOwner().getId().equals(user.getId())) {
            throw new NotFoundException("booker mustn't be owner");
        }
        if (item.getAvailable()) {
            Booking booking = Booking.builder()
                    .start(bookingDto.getStart())
                    .end(bookingDto.getEnd())
                    .item(item)
                    .booker(user)
                    .status(BookingStatus.WAITING)
                    .build();
            log.info("BookingService: create implementation. User ID {}, booking ID {}.", userId, bookingDto.getItemId());
            return BookingMapper.toOutputBookingDto(bookingRepository.save(booking));
        }  else {
            throw new ValidationException("item not available");
        }
    }

    @Override
    @Transactional
    public OutputBookingDto updateBooking(Long bookingId, Long userId, Boolean approve) {
        Booking booking = getBooking(bookingId, userId);
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("status already APPROVED");
        }
        checkBooking(booking, userId, false);
        booking.setStatus(approve ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        bookingRepository.updateStatus(booking.getStatus(), bookingId);
        log.info("BookingService: updateBooking implementation. User ID {}, booking ID {}.", userId, bookingId);
        return BookingMapper.toOutputBookingDto(bookingRepository.save(booking));
    }

    @Override
    public Booking getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("booking not found"));
        checkBooking(booking, userId, true);
        log.info("BookingService: getBooking implementation. User ID {}, booking ID {}.", userId, bookingId);
        return booking;
    }

    @Transactional(readOnly = true)
    @Override
    public OutputBookingDto getBookingDto(Long bookingId, Long userId) {
        Booking booking = getBooking(bookingId, userId);
        log.info("BookingService: getBookingDto implementation. User ID {}, booking ID {}.", userId, bookingId);
        return BookingMapper.toOutputBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<OutputBookingDto> getBookingBooker(BookingState state, Long bookerId) {
        userRepository.findById(bookerId);
        List<Booking> bookings;
        switch (state) {
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.WAITING);
                log.info("BookingService: findBookingBooker implementation. bookerId {}, state {}.",
                        bookerId, state);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.REJECTED);
                log.info("BookingService: findBookingBooker implementation. bookerId {}, state {}.",
                        bookerId, state);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBefore(bookerId, LocalDateTime.now());
                log.info("BookingService: findABookingBooker implementation. bookerId {}, state {}.",
                        bookerId, state);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfter(bookerId, LocalDateTime.now());
                log.info("BookingService: findBookingBooker implementation. bookerId {}, state {}.",
                        bookerId, state);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(bookerId, LocalDateTime.now());
                log.info("BookingService: findBookingBooker implementation. bookerId {}, state {}.",
                        bookerId, state);
                break;
            default:
                bookings = bookingRepository.findAllByBookerId(bookerId);
                log.info("BookingService: findBookingBooker implementation. User ID {}, state by default.",
                        bookerId);
        }
        bookings.sort(Comparator.comparing(Booking::getStart).reversed());
        if (bookings.isEmpty()) throw new NotFoundException("booking owner no found");
        return BookingMapper.toOutputsBookingDtoList(bookings);
    }

    @Transactional(readOnly = true)
    @Override
    public List<OutputBookingDto> getBookingOwner(BookingState state, Long ownerId) {
        userRepository.findById(ownerId);
        List<Booking> bookings;
        switch (state) {
            case WAITING:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(ownerId, BookingStatus.WAITING);
                log.info("BookingService: getBookingOwner implementation. bookerId {}, state {}.",
                        ownerId, state);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(ownerId, BookingStatus.REJECTED);
                log.info("BookingService: getBookingOwner implementation. bookerId {}, state {}.",
                        ownerId, state);
                break;
            case PAST:
                bookings = bookingRepository.findAllByOwnerIdAndEndBefore(ownerId, LocalDateTime.now());
                log.info("BookingService: getBookingOwner implementation. bookerId {}, state {}.",
                        ownerId, state);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByOwnerIdAndStartAfter(ownerId, LocalDateTime.now());
                log.info("BookingService: getBookingOwner implementation. bookerId {}, state {}.",
                        ownerId, state);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfter(ownerId, LocalDateTime.now());
                log.info("BookingService: getBookingOwner implementation. bookerId {}, state {}.",
                        ownerId, state);
                break;
            default:
                bookings = bookingRepository.findAllByOwnerId(ownerId);
                log.info("BookingService: getBookingOwner implementation. User ID {}, state by default.",
                        ownerId);
        }
        if (bookings.isEmpty()) throw new NotFoundException("booking owner no found");
        return BookingMapper.toOutputsBookingDtoList(bookings);
    }

    private void checkBooking(Booking booking, long userId, boolean accessForBooker) {
        User booker = booking.getBooker();
        User owner = booking.getItem().getOwner();
        if (booker == null) {
            throw new NotFoundException("booker not found");
        }
        if (owner == null) {
            throw new NotFoundException("owner not found");
        }
        long bookerId = booker.getId();
        long ownerId = owner.getId();
        if (ownerId == userId) {
            log.info("BookingService: checkBooking implementation. User ID {}, owner ID {}.", userId, ownerId);
            return;
        }
        if (accessForBooker && bookerId == userId) {
            log.info("BookingService: checkBooking implementation. User ID {}, booking ID {}.", userId, bookerId);
            return;
        }
        throw new NotFoundException("checkBooking exception");
    }
}