package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto getById(Long id);

    UserDto createUser(UserDto userDto);

    UserDto update(UserDto userDto, long id);

    void delById(long id);

    List<UserDto> getAll(int from, int size);



}