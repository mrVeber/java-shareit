package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.resource.UserData.*;

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    UserService userService;

    @Test
    void getAllTest() throws Exception {
        when(userService.get()).thenReturn(Collections.singletonList(getUserDto()));

        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("test_nazar"))
                .andExpect(jsonPath("$[0].email").value("test_nazar@mail.ru"));
    }

    @Test
    void getByIdTest() throws Exception {
        when(userService.get(1L)).thenReturn(getUserDto());

        mvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("test_nazar"))
                .andExpect(jsonPath("$.email").value("test_nazar@mail.ru"));
    }

    @Test
    void getById_whenNotFound() throws Exception {
        when(userService.get(2L)).thenThrow(new NotFoundException("User with id = 2  not found"));

        mvc.perform(get("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result -> assertEquals("User with id = 2  not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void createTest() throws Exception {
        when(userService.create(getUser())).thenReturn(getUserDto());

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getUserDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("test_nazar"))
                .andExpect(jsonPath("$.email").value("test_nazar@mail.ru"));
    }

    @Test
    void updateTest() throws Exception {
        when(userService.update(1L, getUpdateUser())).thenReturn(getUpdateDtoUser());

        mvc.perform(patch("/users/1").content(objectMapper.writeValueAsString(getUpdateUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("test_updated"))
                .andExpect(jsonPath("$.email").value("updated@mail.ru"));
    }

    @Test
    void create_Test_bad() throws Exception {
        UserDto user = getUserDto();
        user.setEmail("dwa");
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_whenNotFoundTest() throws Exception {
        when(userService.update(1L, getUser()))
                .thenThrow(new NotFoundException("test not found ok"));

        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(getUpdateDtoUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result -> assertEquals("test not found ok",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void deleteTest() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}
