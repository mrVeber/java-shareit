package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingDtoFullResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoFullResponse create(
            @RequestHeader(name = "X-Sharer-User-Id") long userId,
            @RequestBody BookingDtoCreate bookingDtoCreate) {
        return bookingService.create(userId, bookingDtoCreate);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoFullResponse update(
            @PathVariable long bookingId,
            @RequestParam boolean approved,
            @RequestHeader(name = "X-Sharer-User-Id") long ownerId) {
        return bookingService.update(bookingId, ownerId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoFullResponse get(
            @PathVariable long bookingId,
            @RequestHeader(name = "X-Sharer-User-Id") long ownerId) {
        return bookingService.get(bookingId, ownerId);
    }

    @GetMapping
    public List<BookingDtoFullResponse> getBookings(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(name = "X-Sharer-User-Id") long ownerId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return bookingService.getBookings(ownerId, state, pageRequest);
    }

    @GetMapping("/owner")
    public List<BookingDtoFullResponse> getBookingFromOwner(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(name = "X-Sharer-User-Id") long ownerId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return bookingService.getBookingFromOwner(ownerId, state, pageRequest);
    }
}