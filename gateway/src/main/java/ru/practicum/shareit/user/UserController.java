package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.validators.*;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @Validated(Create.class) @RequestBody UserDto userDto) {
        return userClient.create(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(
            @PathVariable Long id,
            @Validated(Update.class) @RequestBody UserDto userDto) {
        log.info("Received a request to update a user with id {}", id);
        return userClient.update(id, userDto);
    }

    @GetMapping
    public ResponseEntity<Object> get() {
        log.info("Received a request to get all users");
        return userClient.get();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(
            @PathVariable Long id) {
        log.info("Received a request to get user with id: {} ", id);
        return userClient.get(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable long id) {
        return userClient.delete(id);
    }
}
