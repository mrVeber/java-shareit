package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoRQ;
import ru.practicum.shareit.request.dto.ItemRequestDtoRS;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    ItemRequestService itemRequestService;

    User user = new User(
            1L,
            "name",
            "email@email.ru");
    ItemRequestDtoRS itemRequestDtoRS = new ItemRequestDtoRS(
            1L,
            1L,
            "description",
            LocalDateTime.now(),
            new ArrayList<>());
    ItemRequestDtoRQ itemRequestDtoRQ = new ItemRequestDtoRQ(
            1L,
            1L,
            "description",
            LocalDateTime.now()
    );

    @Test
    void createTest() throws Exception {
        when(itemRequestService.create(anyLong(), any())).thenReturn(itemRequestDtoRS);

        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemRequestDtoRS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.requesterId").value(1L))
                .andExpect(jsonPath("$.description").value("description"));
    }

    @Test
    void getRequestsInfoTest() throws Exception {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoRQ, user);
        ItemRequestDtoRS req = ItemRequestMapper.toItemRequestDtoRS(itemRequest);
        when(itemRequestService.getRequests(anyLong(), anyInt(), anyInt())).thenReturn(Collections.singletonList(req));

        mvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getRequestInfoTest() throws Exception {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoRQ, user);
        ItemRequestDtoRS req = ItemRequestMapper.toItemRequestDtoRS(itemRequest);
        when(itemRequestService.getInfo(anyLong(), anyLong())).thenReturn(req);

        mvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requesterId").value(1L))
                .andExpect(jsonPath("$.description").value("description"));
    }

    @Test
    void getRequestsListTest() throws Exception {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoRQ, user);
        ItemRequestDtoRS req = ItemRequestMapper.toItemRequestDtoRS(itemRequest);
        when(itemRequestService.getRequests(anyLong(), anyInt(), anyInt())).thenReturn(Collections.singletonList(req));

        mvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requesterId").value(1L))
                .andExpect(jsonPath("$[0].description").value("description"));
    }

    @Test
    void getRequestsListTest_Bad() throws Exception {
        mvc.perform(get("/requests/all?from=0&size=0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }
}
