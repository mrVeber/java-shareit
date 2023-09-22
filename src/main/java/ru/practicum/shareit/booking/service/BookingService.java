package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForReturn;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingDtoForReturn postBooking(BookingDto bookingDto);

    BookingDtoForReturn approving(long bookingId, long userId, Boolean status);

    BookingDtoForReturn getById(long id, long userId);

    List<BookingDtoForReturn> getByBookerId(long userId, BookingState state, int from, int size);

    List<BookingDtoForReturn> getByOwnerId(long ownerId, BookingState state, int from, int size);
}