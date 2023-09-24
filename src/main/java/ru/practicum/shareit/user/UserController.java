package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.user.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.validators.Create;
import ru.practicum.shareit.validators.Update;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private final UserServiceImpl userService;

    @PostMapping
    public UserDto createUser(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("UserController - createUser(). Создан {}", userDto.toString());
        return userService.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id, @Validated(Update.class) @RequestBody UserDto userDto) {
        userDto.setId(id);
        log.info("UserController - update(). Обновлен {}", userDto.toString());
        return userService.update(userDto, id);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        UserDto userDtoForReturn = userService.getById(userId);
        log.info("UserController - getUser(). Возвращен {}", userDtoForReturn.toString());
        return userDtoForReturn;
    }

    @GetMapping
    public List<UserDto> getAll(@RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                   @RequestParam(name = "size", defaultValue = "10") @Min(1) @Max(100) Integer size) {
        List<UserDto> userDtos = userService.getAll(from, size);
        log.info("UserController - getAll(). Возвращен список из {} пользователей", userDtos.size());
        return userDtos;
    }


    @DeleteMapping("/{id}")
    public void delById(@PathVariable Long id) {
        log.info("UserController - delById(). Удален пользователь с id {}", id);
        userService.delById(id);
    }
}