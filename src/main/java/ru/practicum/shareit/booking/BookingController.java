package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingService;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.validators.Create;

import java.util.List;

import static ru.practicum.shareit.utils.Constants.X_SHARER_USER_ID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOut saveNewBooking(@Validated(Create.class) @RequestBody BookingDtoIn bookingDtoIn,
                                        @RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("POST / bookings");
        return bookingService.saveNewBooking(bookingDtoIn, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut approve(@PathVariable long bookingId, @RequestParam(name = "approved") Boolean isApproved,
                                 @RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("PATCH / bookings / {}", bookingId);
        return bookingService.approve(bookingId, isApproved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut getBookingById(@PathVariable long bookingId, @RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("GET / bookings / {}", bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoOut> getAllByBooker(@RequestParam(defaultValue = "1") Integer from,
                                              @RequestParam(defaultValue = "10") Integer size,
                                              @RequestParam(name = "state", defaultValue = "ALL") String state,
                                              @RequestHeader(X_SHARER_USER_ID) long bookerId) {
        log.info("GET / ByBooker {}", bookerId);
        return bookingService.getAllByBooker(from, size, state, bookerId);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getAllByOwner(@RequestParam(defaultValue = "1") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size,
                                             @RequestParam(name = "state", defaultValue = "ALL") String state,
                                             @RequestHeader(X_SHARER_USER_ID) long ownerId) {
        log.info("GET / ByOwner / {}", ownerId);
        return bookingService.getAllByOwner(from, size, state, ownerId);
    }
}