package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    UserServiceImpl userService;
    @Autowired
    private MockMvc mvc;

    private final long userId = 10L;
    private final String userName = "userUser";
    private final String userMail = "user@mail.com";
    private final UserDtoRequest requestUserDto = UserDtoRequest.builder().name(userName).email(userMail).build();
    private final UserDtoResponse responseUserDto = UserDtoResponse.builder().id(userId).name(userName).email(userMail)
            .build();

    private final String newUserName = "newUser";
    private final String newUserMail = "newUser@mail.com";
    private final UserDtoRequest newDataUserDto = UserDtoRequest.builder().name(newUserName).email(newUserMail).build();
    private final UserDtoResponse newResponseUserDto = UserDtoResponse.builder().id(userId).name(newUserName)
            .email(newUserMail).build();

    private List<UserDtoResponse> getResponseUserDtoList(UserDtoResponse responseUserDto) {
        List<UserDtoResponse> responseUserDtoList = new ArrayList<>();
        responseUserDtoList.add(responseUserDto);
        return responseUserDtoList;
    }

    @Test
    void testCreateUser() throws Exception {
        when(userService.create(any()))
                .thenReturn(responseUserDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(requestUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId), Long.class))
                .andExpect(jsonPath("$.name", is(requestUserDto.getName())))
                .andExpect(jsonPath("$.email", is(requestUserDto.getEmail())));
    }

    @Test
    void testGetUserById() throws Exception {
        when(userService.getById(anyLong()))
                .thenReturn(responseUserDto);

        mvc.perform(get("/users/{id}", userId)
                        .content(mapper.writeValueAsString(responseUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService).getById(userId);
    }

    @Test
    void testGetAllUsers() throws Exception {
        List<UserDtoResponse> responseUserDtoList = getResponseUserDtoList(responseUserDto);
        when(userService.getAll())
                .thenReturn(responseUserDtoList);

        String userDtoListString = mvc.perform(get("/users")
                        .content(mapper.writeValueAsString(responseUserDtoList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(responseUserDtoList), userDtoListString);
    }

    @Test
    void testUpdateUser() throws Exception {
        when(userService.update(anyLong(), any()))
                .thenReturn(newResponseUserDto);

        mvc.perform(MockMvcRequestBuilders.patch("/users/{id}", userId)
                        .content(mapper.writeValueAsString(newDataUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId), Long.class))
                .andExpect(jsonPath("$.name", is(newDataUserDto.getName())))
                .andExpect(jsonPath("$.email", is(newDataUserDto.getEmail())));
    }

    @Test
    void testDeleteUser() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/users/{id}", userId))
                .andExpect(status().isOk());
        verify(userService).delete(userId);
    }
}
