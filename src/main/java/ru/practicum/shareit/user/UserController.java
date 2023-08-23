package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validators.Create;
import ru.practicum.shareit.validators.Update;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto addUser(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.debug("Received request to add new user.");

        return userService.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@Validated(Update.class) @PathVariable(value = "userId") long userId,
                              @RequestBody UserDto userDto) {
        log.debug("Received request to update existed information about user with id {}.", userId);

        return userService.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable(value = "userId") long userId) {
        log.debug("Received request to get existed user with id {}.", userId);

        return userService.getUserDtoById(userId);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.debug("Received request to get all users.");

        return userService.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable(value = "userId") long userId) {
        log.debug("Received request to delete user with id {}.", userId);

        userService.deleteUser(userId);
    }
}
