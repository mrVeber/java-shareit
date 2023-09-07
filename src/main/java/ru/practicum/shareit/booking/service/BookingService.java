package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;
public interface BookingService {
    BookingDto create(long userId, BookingRequestDto createBookingDto);

    BookingDto update(long ownerId, long bookingId, BookingStatus status);

    BookingDto get(long userId, long bookingId);

    List<BookingDto> getAllByBooker(long userId, String state);

    List<BookingDto> getAllByOwner(long ownerId, String status);

}
