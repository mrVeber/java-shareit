package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.*;

import java.util.List;

public interface UserService {

    UserDtoResponse create(UserDtoRequest userDto);

    UserDtoResponse getById(long id);

    List<UserDtoResponse> getAll();

    UserDtoResponse update(long id, UserDtoRequest userDto);

    void delete(long id);



}