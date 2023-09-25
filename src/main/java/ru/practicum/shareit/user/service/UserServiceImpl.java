package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.*;
import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Пользователь " +
                "с id = %s отсутствует в БД. Выполнить операцию невозможно!", id)));
    }

    @Override
    public UserDtoResponse create(UserDtoRequest userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        log.info("Данные пользователя добавлены в БД: {}.", user);
        UserDtoResponse responseUserDto = UserMapper.toResponseUserDto(user);
        log.info("Новый пользователь создан: {}.", responseUserDto);
        return responseUserDto;
    }

    @Override
    public UserDtoResponse getById(long id) {
        User user = getUserById(id);
        log.info("Пользователь найден в БД: {}.", user);
        UserDtoResponse userDto = UserMapper.toResponseUserDto(user);
        log.info("Данные пользователя получены: {}.", userDto);
        return userDto;
    }

    @Override
    public List<UserDtoResponse> getAll() {
        log.info("Получение данных всех пользователей из БД.");
        List<UserDtoResponse> allUserDto = userRepository.findAll()
                .stream()
                .map(UserMapper::toResponseUserDto)
                .collect(Collectors.toList());
        log.info("Сформирован список всех пользователей в количестве: {}.", allUserDto.size());
        return allUserDto;
    }

    @Override
    public UserDtoResponse update(long id, UserDtoRequest userDto) {
        User user = getUserById(id);
        log.info("Пользователь найден в БД: {}.", user);
        User newDataUser = userRepository.save(UserMapper.toUpdatedUser(user, userDto));
        log.info("Данные пользователя обновлены в БД: {}.", newDataUser);
        UserDtoResponse responseUserDto = UserMapper.toResponseUserDto(newDataUser);
        log.info("Данные пользователя обновлены: {}.", responseUserDto);
        return responseUserDto;
    }

    @Override
    public void delete(long id) {
        User user = getUserById(id);
        log.info("Пользователь найден в БД: {}.", user);
        userRepository.delete(user);
        log.info("Все данные пользователя удалены.");
    }
}