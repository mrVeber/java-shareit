package ru.practicum.shareit.booking.service;

import java.util.List;

import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

public interface BookingService {

    OutputBookingDto create(InputBookingDto bookingDto, Long userId);

    OutputBookingDto updateBooking(Long bookingId, Long userId, Boolean approve);

    Booking getBooking(Long bookingId, Long userId);

    OutputBookingDto getBookingDto(Long bookingId, Long userId);

    List<OutputBookingDto> getBookingBooker(BookingState state, Long bookerId);

    List<OutputBookingDto> getBookingOwner(BookingState state, Long ownerId);
}