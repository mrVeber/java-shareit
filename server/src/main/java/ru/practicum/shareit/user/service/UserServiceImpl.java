package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> get() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto get(long id) {
        return UserMapper.toUserDto(userRepository.findById(id).orElseThrow(()
                -> new NotFoundException("User with id = " + id + " not found")));
    }

    @Override
    public UserDto create(User user) {
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto update(long id, User user) {
        User userInMem = userRepository.findById(id).orElseThrow(()
                -> new NotFoundException("User with id = " + id + " not found"));

        if (!Objects.isNull(user.getEmail())
                && !user.getEmail().isBlank()) {
            userInMem.setEmail(user.getEmail());
        }
        if (!Objects.isNull(user.getName()) && !user.getName().isBlank()) {
            userInMem.setName(user.getName());
        }

        return UserMapper.toUserDto(userInMem);
    }

    @Override
    public ResponseEntity<Object> delete(long id) {
        userRepository.findById(id).ifPresent(userRepository::delete);
        return ResponseEntity.ok().build();
    }
}
