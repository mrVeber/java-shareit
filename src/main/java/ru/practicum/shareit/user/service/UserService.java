package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    UserDto create(User user);

    UserDto update(long id, Map<String, String> userDto);

    User getUser(long id);

    UserDto getUserDto(long id);

    List<UserDto> getAll();

    void delete(long userId);

    void deleteAll();
}
