package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class InMemoryUserDao implements UserDao {
    private final Map<Long, User> users;
    private long idCounter = 1;

    @Override
    public User addUser(User user) {
        log.debug("Received user to add as new one.");
        long newUserId = generateId();

        user.setId(newUserId);
        users.put(newUserId, user);

        log.debug("New user with id {} added successfully.", newUserId);
        return user;
    }

    @Override
    public User updateUser(User user) {
        log.debug("Received new information for user with id {}.", user.getId());

        users.put(user.getId(), user);

        log.debug("User with id {} name updates successfully.", user.getId());
        return user;
    }

    @Override
    public Optional<User> getUserById(long userId) {
        log.debug("Received request to get user with id {}.", userId);
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("Received request to get all users.");
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUser(long userId) {
        log.debug("Received request to delete user with id {}.", userId);
        users.remove(userId);
    }

    private long generateId() {
        return idCounter++;
    }
}
