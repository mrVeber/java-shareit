package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.validators.Create;
import ru.practicum.shareit.request.dto.ItemRequestDtoRQ;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.utils.Constant.USER_ID_HEADER;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(USER_ID_HEADER) long userId,
            @Validated(Create.class) @RequestBody ItemRequestDtoRQ itemRequestDtoRQ) {
        return itemRequestClient.create(userId, itemRequestDtoRQ);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsInfo(
            @RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestClient.getInfo(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestInfo(
            @RequestHeader(USER_ID_HEADER) long userId,
            @PathVariable long requestId) {
        return itemRequestClient.getInfo(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsList(
            @RequestHeader(USER_ID_HEADER) long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size) {
        return itemRequestClient.getRequestsList(userId, from, size);
    }
}
