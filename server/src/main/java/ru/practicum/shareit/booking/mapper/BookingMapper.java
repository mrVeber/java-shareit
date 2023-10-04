package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingDtoFullResponse;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {
    public BookingDtoResponse toBookingDtoResponse(Booking booking) {
        return BookingDtoResponse.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public Booking toBooking(BookingDtoCreate bookingDtoCreate, Item inputItem, User inputUser) {
        return Booking.builder()
                .id(bookingDtoCreate.getId())
                .start(bookingDtoCreate.getStart())
                .end(bookingDtoCreate.getEnd())
                .item(inputItem)
                .booker(inputUser)
                .build();
    }

    public BookingDtoFullResponse toBookingRS(Booking booking) {
        return BookingDtoFullResponse.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(toItemDto(booking.getItem()))
                .booker(toBookerDto(booking.getBooker()))
                .build();
    }

    private BookingDtoFullResponse.Item toItemDto(Item item) {
        return new BookingDtoFullResponse.Item(item.getId(), item.getName());
    }

    private BookingDtoFullResponse.Booker toBookerDto(User user) {
        return new BookingDtoFullResponse.Booker(user.getId(), user.getName());
    }
}