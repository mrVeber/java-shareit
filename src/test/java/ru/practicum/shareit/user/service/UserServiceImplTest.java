package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    private final long userId = 10L;
    private final String userName = "userUser";
    private final String userMail = "user@mail.com";
    private final UserDtoRequest requestUserDto = UserDtoRequest.builder().name(userName).email(userMail).build();
    private final User user = User.builder().id(userId).name(userName).email(userMail).build();
    private final UserDtoResponse responseUserDto = UserDtoResponse.builder().id(userId).name(userName)
            .email(userMail).build();

    private final UserDtoRequest requestDuplicateUserDto = UserDtoRequest.builder().name(userName).email(userMail).build();

    private final String newUserName = "newUser";
    private final String newUserMail = "newUser@mail.com";
    private final UserDtoRequest newDataUserDto = UserDtoRequest.builder().name(newUserName).email(newUserMail).build();
    private final User newDataUser = User.builder().id(userId).name(newUserName).email(newUserMail).build();
    private final UserDtoResponse newResponseUserDto = UserDtoResponse.builder().id(userId).name(newUserName)
            .email(newUserMail).build();

    private final long notExistingUserId = 9999L;

    @Test
    void testCreateUser() {
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(user);

        assertEquals(userService.create(requestUserDto), responseUserDto);
    }

    @Test
    void testCreateUserWithDuplicateEmail() {
        Mockito.when(userRepository.save(Mockito.any()))
                .thenThrow(new ValidationException("Пользователь с такой же эл.почтой уже существует! " +
                        "Выполнить операцию невозможно."));
        Exception exception = assertThrows(ValidationException.class, () -> userService.create(requestDuplicateUserDto));

        assertEquals("Пользователь с такой же эл.почтой уже существует! Выполнить операцию невозможно.",
                exception.getMessage());
    }

    @Test
    void testGetUserById() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));

        assertEquals(userService.getById(userId), responseUserDto);
    }

    @Test
    void testGetUserByNotExistingId() {
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenThrow(new NotFoundException("Пользователь с id = 9999 отсутствует в БД. " +
                        "Выполнить операцию невозможно!"));
        Exception exception = assertThrows(NotFoundException.class, () -> userService.getById(notExistingUserId));

        assertEquals("Пользователь с id = 9999 отсутствует в БД. Выполнить операцию невозможно!",
                exception.getMessage());
    }

    @Test
    void testGetAllUsers() {
        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(user));

        assertEquals(userService.getAll(), List.of(responseUserDto));
    }

    @Test
    void testGetAllUsersEmpty() {
        Mockito.when(userRepository.findAll())
                .thenReturn(new ArrayList<>());

        assertEquals(userService.getAll(), new ArrayList<>());
    }

    @Test
    void testUpdateUser() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(newDataUser);

        assertEquals(userService.update(userId, newDataUserDto), newResponseUserDto);
    }

    @Test
    void testUpdateNotExistUser() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundException("Пользователь с id = 9999 отсутствует в БД. " +
                        "Выполнить операцию невозможно!"));
        Exception exception = assertThrows(NotFoundException.class, () -> userService.update(notExistingUserId,
                newDataUserDto));

        assertEquals("Пользователь с id = 9999 отсутствует в БД. Выполнить операцию невозможно!",
                exception.getMessage());
    }

    @Test
    void testUpdateUserWithDuplicateEmail() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepository.save(Mockito.any()))
                .thenThrow(new ValidationException("Пользователь с такой же эл.почтой уже существует! " +
                        "Выполнить операцию невозможно."));
        Exception exception = assertThrows(ValidationException.class,
                () -> userService.update(userId, requestDuplicateUserDto));

        assertEquals("Пользователь с такой же эл.почтой уже существует! Выполнить операцию невозможно.",
                exception.getMessage());
    }

    @Test
    void testDeleteUser() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));
        userService.delete(userId);

        Mockito.verify(userRepository, Mockito.times(1)).delete(user);
    }

    @Test
    void testDeleteNotExistUser() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundException("Пользователь с id = 9999 отсутствует в БД. " +
                        "Выполнить операцию невозможно!"));
        Exception exception = assertThrows(NotFoundException.class, () -> userService.delete(notExistingUserId));

        assertEquals("Пользователь с id = 9999 отсутствует в БД. Выполнить операцию невозможно!",
                exception.getMessage());
    }
}
