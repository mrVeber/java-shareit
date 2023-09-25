package ru.practicum.shareit.user.controller;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.user.dto.*;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.validators.Create;
import ru.practicum.shareit.validators.Update;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserServiceImpl userService;

    @PostMapping
    public UserDtoResponse createUser(@RequestBody @Validated(Create.class) UserDtoRequest userDto) {
        log.info("");
        log.info("Добавление нового пользователя: {}", userDto);
        return userService.create(userDto);
    }

    @GetMapping("/{id}")
    public UserDtoResponse getUserById(@PathVariable long id) {
        log.info("");
        log.info("Получение данных пользователя с id = {}", id);
        return userService.getById(id);
    }

    @GetMapping
    public List<UserDtoResponse> getAllUsers() {
        log.info("");
        log.info("Поиск всех пользователей");
        return userService.getAll();
    }

    @PatchMapping("/{id}")
    public UserDtoResponse updateUser(@PathVariable long id,
                                      @RequestBody @Validated(Update.class) UserDtoRequest userDto) {
        log.info("");
        log.info("Обновление данных пользователя с id = {}: {}", id, userDto);
        return userService.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        log.info("");
        log.info("Удаление всех данных пользователя c id = {}", id);
        userService.delete(id);
    }
}