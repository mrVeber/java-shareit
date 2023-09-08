package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(User user) {
        log.info("UserService: create implementation. User ID {}.", user.getId());
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto update(long id, Map<String, String> updates) {
        User user = getUser(id);
        if (updates.containsKey("name")) {
            String name = updates.get("name");
            user.setName(name.trim());
        }
        if (updates.containsKey("email")) {
            String email = updates.get("email");
            user.setEmail(email);
        }
        userRepository.save(user);
        log.info("UserService: update implementation. User ID {}.", user.getId());
        return UserMapper.toUserDto(getUser(id));
    }

    @Transactional(readOnly = true)
    @Override
    public User getUser(long id) {
        log.info("UserService: findById implementation. User ID {}.", userRepository.findById(id));
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User user"));
    }

    @Override
    public UserDto getUserDto(long id) {
        log.info("UserService: getUserDto implementation. User ID {}.", userRepository.findById(id));
        return UserMapper.toUserDto(getUser(id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAll() {
        log.info("UserService: getAll implementation");
        return UserMapper.toUserDtoList(userRepository.findAll());
    }

    @Transactional
    @Override
    public void delete(long userId) {
        log.info("UserService: delete implementation. User ID {}.", userId);
        userRepository.deleteById(userId);
    }

    @Transactional
    @Override
    public void deleteAll() {
        log.info("UserService: deleteAll implementation.");
        userRepository.deleteAll();
    }
}
