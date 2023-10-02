package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingDtoFullResponse;

import java.util.List;

public interface BookingService {
    BookingDtoFullResponse create(long userId, BookingDtoCreate bookingDtoCreate);

    BookingDtoFullResponse update(long bookingId, long ownerId, boolean approved);

    BookingDtoFullResponse get(long bookingId, long ownerId);

    List<BookingDtoFullResponse> getBookings(long ownerId, String state, PageRequest pageRequest);

    List<BookingDtoFullResponse> getBookingFromOwner(long ownerId, String state, PageRequest pageRequest);

}
