package ru.practicum.shareit.booking.service;

import java.util.List;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBooking;

public interface BookingService {

    BookingDto createBooking(NewBooking bookingDto, Long userId);

    BookingDto approveOrRejected(Long bookingId, boolean approved, long userId);

    BookingDto getBookingById(Long bookingId, Long userID);

    List<BookingDto> getAllBookingByUserId(Long userId, String state);

    List<BookingDto> getBookingsOfOwner(Long ownerId, String state);
}