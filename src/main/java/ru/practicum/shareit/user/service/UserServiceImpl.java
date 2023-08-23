package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.AlreadyUsedException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserDao userRepository;

    @Override
    public UserDto addUser(UserDto userDto) {
        if (!isEmailAvailable(userDto.getEmail())) {
            throw new AlreadyUsedException("Email already used.");
        }
        log.debug("Sending to DAO information to add new user.");
        return UserMapper.convertToDto(userRepository.addUser(UserMapper.convertToUser(userDto)));
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        checkIsUserPresent(userId);
        User user = getUserById(userId);
        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail()) && !userDto.getEmail().isEmpty()) {
            if (!isEmailAvailable(userDto.getEmail())) {
                throw new AlreadyUsedException("Email already used.");
            }
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        log.debug("Sending to DAO updated user {} information.", userId);
        return UserMapper.convertToDto(userRepository.updateUser(user));
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.debug("Sending to DAO request to get all users.");

        return userRepository.getAllUsers().stream()
                .map(UserMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserDtoById(long userId) {
        log.debug("Sending to DAO request to get user with id {}.", userId);

        return UserMapper.convertToDto(userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " does not present in repository.")));
    }

    @Override
    public void checkIsUserPresent(long userId) {
        userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " does not present in repository."));
    }

    @Override
    public void deleteUser(long userId) {
        checkIsUserPresent(userId);

        log.debug("Sending to DAO request to delete user with id {}.", userId);

        userRepository.deleteUser(userId);
    }

    private User getUserById(long userId) {
        return userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " does not present in repository."));
    }

    public boolean isEmailAvailable(String email) {
        List<String> emails = userRepository.getAllUsers().stream()
                .map(User::getEmail)
                .collect(Collectors.toList());

        return !emails.contains(email);
    }
}
