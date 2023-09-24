package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForReturn;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Override
    @Transactional
    public BookingDtoForReturn postBooking(BookingDto bookingDto) {
        Optional<User> booker = userRepository.findById(bookingDto.getBooker());
        if (booker.isEmpty()) {
            throw new NotExistInDataBase("Booker не обнаружен в базе данных");
        }

        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        if (item.isEmpty()) {
            throw new NotFoundException("Item не обнаружен в базе данных");
        }

        if (booker.get().getId().equals(item.get().getOwner())) {
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }
        checkAvailable(item.get());

        checkTimeCreate(BookingMapper.toBooking(bookingDto, booker.get(), item.get()));
        return BookingMapper.toBookingDtoForReturn(bookingRepository.save(BookingMapper.toBooking(bookingDto, booker.get(), item.get())));
    }

    private void checkAvailable(Item item) {
        if (!item.getAvailable()) {
            throw new AvailableCheckException("Вещь в данный момент не доступна");
        }
    }

    @Override
    @Transactional
    public BookingDtoForReturn approving(long bookingId, long userId, Boolean status) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException("Booking не найден");
        });

        Item item = itemRepository.getById(booking.getItem().getId()); // получили из репозитория

        checkOwner(userId, item.getOwner());

        if (status == null) throw new IllegalArgumentException("Status не может быть null! Укажите статус");

        if (item.getOwner() != userId) {
            throw new NotFoundException("Данные User не может подтвердить бронирование");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new AvailableCheckException("Бронирование подтверждено ранее. Изменение больше не доступно.");
        }
        if (status) {
            booking.setStatus(BookingStatus.APPROVED);
        } else booking.setStatus(BookingStatus.REJECTED);
        log.info("BookingService - approving().  Подтверждено  {}", booking.toString());
        checkTimeUpdate(booking);
        return BookingMapper.toBookingDtoForReturn(booking);
    }

    private void checkTimeCreate(Booking booking) {
        if (booking.getEnd().isBefore(booking.getStart()) ||
                booking.getEnd().isEqual(booking.getStart())) {
            throw new AvailableCheckException("Период бронирования задан не корректно");
        }
    }

    private void checkTimeUpdate(Booking booking) {
        if (!booking.getEnd().isAfter(booking.getStart())) {
            throw new AvailableCheckException("Период бронирования задан не корректно");
        }
    }

    private void checkOwner(long userId, long ownerId) {
        if (userId != ownerId) {
            throw new NotFoundException("Данные User не может подтвердить бронирование");
        }
    }

    @Override
    public BookingDtoForReturn getById(long id, long userId) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Бронирование отсутствует");
        });
        if (booking.getItem().getOwner() != userId && booking.getBooker().getId() != userId) {
            throw new NotFoundException("Юзеру не доступна информация о бронировании");
        }
        BookingDtoForReturn bookingDtoForReturn = BookingMapper.toBookingDtoForReturn(booking);
        log.info("BookingService - getById(). Возвращено  {}", booking.toString());
        return bookingDtoForReturn;
    }

    @Override
    public List<BookingDtoForReturn> getByBookerId(long userId, BookingState state, int from, int size) {

        if (!userRepository.existsById(userId)) {
            throw new NotExistInDataBase("User не существует");
        }
        switch (state) {
            case ALL:
                return bookingRepository.findAllByBookerOrderByStartDesc(userId, PageRequest.of(from / size, size))
                        .stream().map(BookingMapper::toBookingDtoForReturn).collect(Collectors.toList());

            case FUTURE:
                return bookingRepository.findAllByBookerOrderByStartDescFuture(userId, LocalDateTime.now(), sort)
                        .stream().map(BookingMapper::toBookingDtoForReturn).collect(Collectors.toList());

            case WAITING:
                return bookingRepository.findAllByBookerOrderByStartDescStatus(userId, BookingStatus.WAITING)
                        .stream().map(BookingMapper::toBookingDtoForReturn).collect(Collectors.toList());

            case REJECTED:
                return bookingRepository.findAllByBookerOrderByStartDescStatus(userId, BookingStatus.REJECTED)
                        .stream().map(BookingMapper::toBookingDtoForReturn).collect(Collectors.toList());

            case CURRENT:
                return bookingRepository.findAllByBookerOrderByStartDescCurrent(userId, LocalDateTime.now())
                        .stream().map(BookingMapper::toBookingDtoForReturn).collect(Collectors.toList());

            case PAST:
                return bookingRepository.findAllByBookerPast(userId, LocalDateTime.now())
                         .stream().map(BookingMapper::toBookingDtoForReturn).collect(Collectors.toList());
            default:
                throw new UnknownStatusException("Unknown state: " + "UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingDtoForReturn> getByOwnerId(long ownerId, BookingState state, int from, int size) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotExistInDataBase("User не существует");
        }
        switch (state) {
            case ALL:
                return bookingRepository.findAllByOwnerOrderByStartDesc(ownerId, PageRequest.of(from, size))
                        .stream().map(BookingMapper::toBookingDtoForReturn).collect(Collectors.toList());

            case FUTURE:
                return bookingRepository.findAllByOwnerOrderByStartDescFuture(ownerId, LocalDateTime.now())
                        .stream().map(BookingMapper::toBookingDtoForReturn).collect(Collectors.toList());

            case WAITING:
                return bookingRepository.findAllByOwnerOrderByStartDescStatus(ownerId, BookingStatus.WAITING)
                        .stream().map(BookingMapper::toBookingDtoForReturn).collect(Collectors.toList());

            case REJECTED:
                return bookingRepository.findAllByOwnerOrderByStartDescStatus(ownerId, BookingStatus.REJECTED)
                        .stream().map(BookingMapper::toBookingDtoForReturn).collect(Collectors.toList());

            case CURRENT:
                return bookingRepository.findAllByOwnerOrderByStartDescCurrent(ownerId, LocalDateTime.now())
                        .stream().map(BookingMapper::toBookingDtoForReturn).collect(Collectors.toList());

            case PAST:
                return bookingRepository.findAllByOwnerPast(ownerId, LocalDateTime.now())
                        .stream().map(BookingMapper::toBookingDtoForReturn).collect(Collectors.toList());
            default:
                throw new UnknownStatusException("Unknown state: " + "UNSUPPORTED_STATUS");
        }
    }
}