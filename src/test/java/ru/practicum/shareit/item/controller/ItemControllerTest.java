package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utils.Constants.X_SHARER_USER_ID;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemService itemService;
    @Autowired
    private MockMvc mvc;

    private final long ownerId = 10L;
    private final String ownerName = "ownerUser";
    private final String ownerMail = "owner@mail.com";
    private final User owner = User.builder().id(ownerId).name(ownerName).email(ownerMail).build();

    private final long oneBookerId = 12;
    private final long twoBookerId = 13;

    private final long authorId = 11;
    private final String testAuthorName = "author";
    private final User author = User.builder().id(authorId).name(testAuthorName).email("author@mail.com").build();

    private final long itemId = 5L;
    private final long oneBookingId = 301;
    private final LocalDateTime startOneBooking = LocalDateTime.of(2023, 9, 1, 12,
            10, 10);
    private final LocalDateTime endOneBooking = LocalDateTime.of(2023, 9, 10, 12,
            10, 10);
    private final ItemBookingDto oneBookingDto = ItemBookingDto.builder().id(oneBookingId).start(startOneBooking)
            .end(endOneBooking).status(BookingStatus.APPROVED).itemId(itemId).bookerId(oneBookerId).build();

    private final long twoBookingId = 302;
    private final LocalDateTime startTwoBooking = LocalDateTime.of(2023, 9, 11, 12,
            10,10);
    private final LocalDateTime endTwoBooking = LocalDateTime.of(2023, 9, 20, 12,
            10,10);
    private final ItemBookingDto twoBookingDto = ItemBookingDto.builder().id(twoBookingId).start(startTwoBooking)
            .end(endTwoBooking).status(BookingStatus.APPROVED).itemId(itemId).bookerId(twoBookerId).build();

    private final String itemName = "itemTest";
    private final String itemDescription = "itemTestTestTest";
    private final ItemDtoRequest requestItemDto = ItemDtoRequest.builder().name(itemName).description(itemDescription)
            .available(true).requestId(0).build();
    private final ItemDtoResponse responseItemDto = ItemDtoResponse.builder().id(itemId).name(itemName)
            .description(itemDescription).available(true).requestId(0).build();
    private final ItemDtoFullResponse fullResponseItemDto = ItemDtoFullResponse.builder().id(itemId).name(itemName)
            .description(itemDescription).available(true).lastBooking(oneBookingDto).nextBooking(twoBookingDto).build();
    private final String newItemName = "newItemTest";
    private final String newItemDescription = "newItemTestTestTest";
    private final ItemDtoRequest newDataItemDto = ItemDtoRequest.builder().name(newItemName)
            .description(newItemDescription).available(true).requestId(0).build();
    private final ItemDtoResponse newResponseItemDto = ItemDtoResponse.builder().id(itemId).name(newItemName)
            .description(newItemDescription).available(true).requestId(0).build();

    private final Item commentItem = Item.builder().id(itemId).name("Test").description("TestTestTestTestTest")
            .available(true).owner(owner).build();

    private final long testCommentId = 205;
    private final CommentDtoRequest requestCommentDto = CommentDtoRequest.builder().text("TestTestTestTestTest").build();

    private final CommentDtoResponse responseCommentDto = CommentDtoResponse.builder().id(testCommentId)
            .text("TestTestTestTestTest").created(LocalDateTime.of(2023, 9, 1, 12, 0))
            .authorName(testAuthorName).build();

    @Test
    void testCreateItem() throws Exception {
        when(itemService.create(any(), anyLong()))
                .thenReturn(responseItemDto);

        mvc.perform(post("/items")
                        .header(X_SHARER_USER_ID, ownerId)
                        .content(mapper.writeValueAsString(requestItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(requestItemDto.getName())))
                .andExpect(jsonPath("$.description", is(requestItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(requestItemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(requestItemDto.getRequestId()), Long.class));
    }

    @Test
    void testGetItemById() throws Exception {
        when(itemService.getById(anyLong(), anyLong()))
                .thenReturn(fullResponseItemDto);

        mvc.perform(get("/items/{itemId}", itemId)
                        .header(X_SHARER_USER_ID, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).getById(itemId, ownerId);
    }

    @Test
    void testGetItemsOneUser() throws Exception {
        List<ItemDtoFullResponse> fullResponseItemDtoList = List.of(fullResponseItemDto);
        when(itemService.getItemsOneOwner(anyLong(), anyInt(), anyInt()))
                .thenReturn(fullResponseItemDtoList);

        String itemDtoListString = mvc.perform(get("/items")
                        .header(X_SHARER_USER_ID, ownerId)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(fullResponseItemDtoList), itemDtoListString);
    }

    @Test
    void testUpdateItem() throws Exception {
        when(itemService.update(anyLong(), any(), anyLong()))
                .thenReturn(newResponseItemDto);

        mvc.perform(MockMvcRequestBuilders.patch("/items/{id}", itemId)
                        .header(X_SHARER_USER_ID, ownerId)
                        .content(mapper.writeValueAsString(newDataItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId), Long.class))
                .andExpect(jsonPath("$.name", is(newDataItemDto.getName())))
                .andExpect(jsonPath("$.description", is(newDataItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(newDataItemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(newDataItemDto.getRequestId()), Long.class));
    }

    @Test
    void testDeleteItem() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/items/{id}", itemId)
                        .header(X_SHARER_USER_ID, ownerId))
                .andExpect(status().isOk());
        verify(itemService).delete(itemId, ownerId);
    }

    @Test
    void testCreateComment() throws Exception {
        when(itemService.createComment(anyLong(), any(), anyLong()))
                .thenReturn(responseCommentDto);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(X_SHARER_USER_ID, ownerId)
                        .content(mapper.writeValueAsString(requestCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorName", is(author.getName())))
                .andExpect(jsonPath("$.text", is(requestCommentDto.getText())));
    }
}
