package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDtoFullResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utils.Constants.X_SHARER_USER_ID;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;

    private final long requestorId = 10;
    private final String requestorName = "requestorUser";
    private final String requestorMail = "requestor@mail.com";
    private final UserDtoResponse responseUserDto = UserDtoResponse.builder().id(requestorId).name(requestorName)
            .email(requestorMail).build();

    private final long requestId = 50;
    private final String requestDescription = "testTestTestTestTest";
    private final LocalDateTime requestDateTime = LocalDateTime.of(2023, 9, 10, 12, 0);
    ItemRequestDtoRequest requestItemRequestDto = ItemRequestDtoRequest.builder().description(requestDescription).build();
    ItemRequestDtoResponse responseItemRequestDto = ItemRequestDtoResponse.builder().id(requestId)
            .description(requestDescription).created(requestDateTime).requestor(responseUserDto).build();
    ItemRequestDtoFullResponse fullResponseItemRequestDto = ItemRequestDtoFullResponse.builder().id(requestId)
            .description(requestDescription).created(requestDateTime).build();

    @Test
    void testCreateItemRequest() throws Exception {
        when(itemRequestService.create(any(), anyLong()))
                .thenReturn(responseItemRequestDto);

        mvc.perform(post("/requests")
                        .header(X_SHARER_USER_ID, requestorId)
                        .content(mapper.writeValueAsString(requestItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestId), Long.class))
                .andExpect(jsonPath("$.description", is(requestItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestor.id", is(requestorId), Long.class));
    }

    @Test
    void testGetRequestsById() throws Exception {
        when(itemRequestService.getById(anyLong(), anyLong()))
                .thenReturn(fullResponseItemRequestDto);

        mvc.perform(get("/requests/{requestId}", requestId)
                        .header(X_SHARER_USER_ID, requestorId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemRequestService).getById(requestId, requestorId);
    }

    @Test
    void testGetOwnItemRequests() throws Exception {
        List<ItemRequestDtoFullResponse> fullResponseItemRequestDtoList = List.of(fullResponseItemRequestDto);
        when(itemRequestService.getOwnItemRequests(anyLong()))
                .thenReturn(fullResponseItemRequestDtoList);

        String requestDtoListString = mvc.perform(get("/requests")
                        .header(X_SHARER_USER_ID, requestorId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(fullResponseItemRequestDtoList), requestDtoListString);
    }

    @Test
    void testGetAllRequests() throws Exception {
        List<ItemRequestDtoFullResponse> fullResponseItemRequestDtoList = List.of(fullResponseItemRequestDto);
        when(itemRequestService.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(fullResponseItemRequestDtoList);

        String requestDtoListString = mvc.perform(get("/requests/all")
                        .header(X_SHARER_USER_ID, requestorId)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(fullResponseItemRequestDtoList), requestDtoListString);
    }
}
