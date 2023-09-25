package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@Rollback
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserIntegrationTest {
    private final EntityManager em;
    private final UserServiceImpl userService;

    private final long userId = 1L;
    private final String userName = "userUser";
    private final String userMail = "user@mail.com";
    private final UserDtoRequest requestUserDto = UserDtoRequest.builder().name(userName).email(userMail).build();

    private final String newUserName = "newUser";
    private final String newUserMail = "newUser@mail.com";
    private final UserDtoRequest newDataUserDto = UserDtoRequest.builder().name(newUserName).email(newUserMail).build();

    @Test
    void testCreateUser() {
        userService.create(requestUserDto);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", requestUserDto.getEmail()).getSingleResult();

        assertThat(user.getId(), equalTo(userId));
        assertThat(user.getName(), equalTo(requestUserDto.getName()));
        assertThat(user.getEmail(), equalTo(requestUserDto.getEmail()));
    }

    @Test
    void testGetUserById() {
        userService.create(requestUserDto);
        UserDtoResponse responseUserDto = userService.getById(userId);

        assertThat(responseUserDto.getId(), equalTo(userId));
        assertThat(responseUserDto.getName(), equalTo(requestUserDto.getName()));
        assertThat(responseUserDto.getEmail(), equalTo(requestUserDto.getEmail()));
    }

    @Test
    void testGetAllUsers() {
        userService.create(requestUserDto);
        List<UserDtoResponse> responseUserDtoList = userService.getAll();
        UserDtoResponse responseUserDto = responseUserDtoList.get(0);

        assertThat(responseUserDtoList.size(), equalTo(1));
        assertThat(responseUserDto.getId(), equalTo(userId));
        assertThat(responseUserDto.getName(), equalTo(requestUserDto.getName()));
        assertThat(responseUserDto.getEmail(), equalTo(requestUserDto.getEmail()));
    }

    @Test
    void testUpdateUser() {
        userService.create(requestUserDto);

        List<UserDtoResponse> responseUserDtoList = userService.getAll();
        assertThat(responseUserDtoList.size(), equalTo(1));
        assertThat(responseUserDtoList.get(0).getEmail(), equalTo(requestUserDto.getEmail()));

        userService.update(userId, newDataUserDto);
        responseUserDtoList = userService.getAll();
        assertThat(responseUserDtoList.size(), equalTo(1));
        assertThat(responseUserDtoList.get(0).getEmail(), equalTo(newDataUserDto.getEmail()));
    }

    @Test
    void testDeleteUser() {
        userService.create(requestUserDto);
        List<UserDtoResponse> responseUserDtoList = userService.getAll();
        assertThat(responseUserDtoList.size(), equalTo(1));
        assertThat(responseUserDtoList.get(0).getEmail(), equalTo(requestUserDto.getEmail()));

        userService.delete(userId);
        responseUserDtoList = userService.getAll();
        assertThat(responseUserDtoList.size(), equalTo(0));
    }
}
