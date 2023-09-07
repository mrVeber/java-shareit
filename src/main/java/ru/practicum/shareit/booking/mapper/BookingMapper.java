package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.StandardBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.user.dto.BookerDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static Booking fromBookingRequestDto(BookingRequestDto createBookingDto) {
        Booking booking = new Booking();
        booking.setStart(createBookingDto.getStart());
        booking.setEnd(createBookingDto.getEnd());
        return booking;
    }

    public static StandardBookingDto toStandardBookingDto(Booking booking) {
        return new StandardBookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new BookerDto(booking.getBooker().getId()),
                new ShortItemDto(booking.getItem().getId(), booking.getItem().getName()));
    }
}
