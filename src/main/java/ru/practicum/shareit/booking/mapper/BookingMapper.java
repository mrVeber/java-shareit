package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForReturn;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserShort;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {
    public Booking toBooking(BookingDto bookingDto, User user, Item item) {
        return new Booking(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user,
                BookingStatus.WAITING);
    }

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId()
        );
    }

    public BookingDtoForReturn toBookingDtoForReturn(Booking booking) {
        return new BookingDtoForReturn(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                new ItemShort(booking.getItem().getId(), booking.getItem().getName()),
                new UserShort(booking.getBooker().getId()),
                booking.getStatus());
    }

    public BookingShortDto toBookingShortDto(Booking booking) {
        return new BookingShortDto(booking.getId(),
                booking.getBooker().getId());
    }
}