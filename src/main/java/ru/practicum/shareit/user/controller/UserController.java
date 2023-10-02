package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.validators.*;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(
            @Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("Received a request to add a new user");
        return userService.create(UserMapper.toUser(userDto));
    }

    @PatchMapping("/{id}")
    public UserDto put(
            @PathVariable Long id,
            @Validated(Update.class) @RequestBody UserDto userDto) {
        log.info("Received a request to update a user with id {}", id);
        return userService.update(id, UserMapper.toUser(userDto));
    }

    @GetMapping
    public List<UserDto> get() {
        log.info("Received a request to get all users");
        return userService.get();
    }

    @GetMapping("/{id}")
    public UserDto get(
            @PathVariable Long id) {
        log.info("Received a request to get user with id: {} ", id);
        return userService.get(id);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id) {
        log.info("Received a request to remove user with id: {} ", id);
        userService.delete(id);
    }
}