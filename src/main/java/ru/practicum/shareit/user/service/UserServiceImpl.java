package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.model.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto getById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("User не обнружен");
        }
        UserDto userDtoForReturn = UserMapper.toUserDto(user.get());
        log.info("UserService - getById(). Возвращен {}", userDtoForReturn.toString());
        return userDtoForReturn;
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        UserDto user = UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
        log.info("UserService - createUser(). ДОбавлен {}", user.toString());
        return user;
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, long id) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.warn("UserService - update().User {} для обновления не существует", id);
            throw new NotFoundException("UserService - update().User для обновления не существует");
        });
        userDto.setId(id);
        prepareUserForUpdate(user, userDto);
        log.info("UserService - update(). Обновлен {}", user.toString());
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void delById(long id) {
        checkUnicId(id);
        log.info("UserService - delById(). Удален пользователь с id {}", id);
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getAll(int from, int size) {

        Page<User> usersPage = userRepository.findAll(PageRequest.of(from, size));
        List<User> users = usersPage.toList();
        List<UserDto> userDtos = users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
        log.info("UserController - getAll(). Возвращен список из {} пользователей", userDtos.size());
        return userDtos;
    }


    private void checkUnicId(long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new ValidationException("Пользователь с таким id не существует");
        }
    }


    private void prepareUserForUpdate(User user, UserDto userDto) {
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            user.setEmail(userDto.getEmail());
        }
        log.info("UserService - Было {} , Стало {}", user.toString(), userDto.toString());
    }
}