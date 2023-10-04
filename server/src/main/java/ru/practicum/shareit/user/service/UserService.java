package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Transactional(readOnly = true)
public interface UserService {

    List<UserDto> getAllUsers();

    UserDto getUserById(long userId);

    UserDto saveNewUser(UserDto userDto);

    UserDto updateUser(long userId, UserDto userDto);

    void deleteUser(long id);
}