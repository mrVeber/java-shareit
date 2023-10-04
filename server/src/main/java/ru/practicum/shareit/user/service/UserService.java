package ru.practicum.shareit.user.service;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Transactional(readOnly = true)
public interface UserService {
    List<UserDto> get();

    UserDto get(long id);

    @Transactional
    UserDto create(User user);

    @Transactional
    UserDto update(long id, User user);

    @Transactional
    ResponseEntity<Object> delete(long id);
}