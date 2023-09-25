package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.*;

import java.util.List;

public interface BookingService {

    BookingDtoResponse create(BookingDtoRequest bookingDto, long userId);

    BookingDtoResponse approve(long id, boolean approved, long userId);

    BookingDtoResponse getById(long id, long userId);

    List<BookingDtoResponse> getBookingsOneBooker(long userId, String state, int from, int size);

    List<BookingDtoResponse> getBookingsOneOwner(long userId, String state, int from, int size);
}