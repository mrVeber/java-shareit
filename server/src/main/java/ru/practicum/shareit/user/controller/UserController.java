package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
            @RequestBody UserDto userDto) {
        return userService.create(UserMapper.toUser(userDto));
    }

    @PatchMapping("/{id}")
    public UserDto put(
            @PathVariable Long id,
            @RequestBody UserDto userDto) {
        return userService.update(id, UserMapper.toUser(userDto));
    }

    @GetMapping
    public List<UserDto> get() {
        return userService.get();
    }

    @GetMapping("/{id}")
    public UserDto get(
            @PathVariable Long id) {
        return userService.get(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(
            @PathVariable Long id) {
        return userService.delete(id);
    }
}