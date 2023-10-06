package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.validators.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.utils.Constant.USER_ID_HEADER;

@Slf4j
@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(name = USER_ID_HEADER) long userId,
            @Validated({Create.class}) @RequestBody BookingDtoCreate bookingDtoCreate) {
        log.info("Received a request to add a booking");
        return bookingClient.create(userId, bookingDtoCreate);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(
            @PathVariable long bookingId,
            @RequestParam boolean approved,
            @RequestHeader(name = USER_ID_HEADER) long ownerId) {
        log.info("Received a request to update the booking");
        return bookingClient.update(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(
            @PathVariable long bookingId,
            @RequestHeader(name = USER_ID_HEADER) long ownerId) {
        log.info("Received a request to get booking with id {}", bookingId);
        return bookingClient.get(bookingId, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(name = USER_ID_HEADER) long ownerId,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "20") int size) {
        log.info("Received a request to get bookings with state {}", state);
        return bookingClient.getBookings(ownerId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingFromOwner(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(name = USER_ID_HEADER) long ownerId,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "20") int size) {
        log.info("Received a request to get bookings with state {}", state);
        return bookingClient.getBookingFromOwner(ownerId, state, from, size);
    }
}
