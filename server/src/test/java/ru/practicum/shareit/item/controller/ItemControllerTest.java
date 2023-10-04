//package ru.practicum.shareit.item.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import ru.practicum.shareit.booking.repository.BookingRepository;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.item.mapper.ItemMapper;
//import ru.practicum.shareit.item.service.ItemService;
//import ru.practicum.shareit.user.model.User;
//import ru.practicum.shareit.user.mapper.UserMapper;
//
//import java.util.Collections;
//import java.util.Objects;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static ru.practicum.shareit.resource.ItemData.getItemDto;
//import static ru.practicum.shareit.resource.ItemData.getItemDtoForUpdate;
//import static ru.practicum.shareit.resource.UserData.getUser;
//import static ru.practicum.shareit.resource.UserData.getUserDto;
//
//@WebMvcTest(controllers = ItemController.class)
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//class ItemControllerTest {
//    private final ObjectMapper objectMapper;
//    private final MockMvc mvc;
//    @MockBean
//    ItemService itemService;
//    @MockBean
//    BookingRepository bookingRepository;
//
//    @Test
//    void getAllTest() throws Exception {
//        User user = UserMapper.toUser(getUserDto());
//        Item item = ItemMapper.toItem(getItemDto(), user, null);
//        ItemDtoBooking itemDtoBooking = ItemMapper.toItemDtoBooking(item);
//
//        when(itemService.getUserItems(anyLong(), any())).thenReturn(Collections.singletonList(itemDtoBooking));
//
//        mvc.perform(get("/items")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .header("X-Sharer-User-Id", 1L))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1L))
//                .andExpect(jsonPath("$[0].name").value("name"))
//                .andExpect(jsonPath("$[0].description").value("description"));
//    }
//
//    @Test
//    void findItemTest() throws Exception {
//        User user = UserMapper.toUser(getUserDto());
//        Item item = ItemMapper.toItem(getItemDto(), user, null);
//        ItemDtoBooking itemDtoBooking = ItemMapper.toItemDtoBooking(item);
//        when(itemService.getById(anyLong(), anyLong())).thenReturn(itemDtoBooking);
//
//        mvc.perform(get("/items/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .header("X-Sharer-User-Id", 1L))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.name").value("name"))
//                .andExpect(jsonPath("$.description").value("description"));
//    }
//
//    @Test
//    void createTest() throws Exception {
//        when(itemService.create(1L, getItemDto())).thenReturn(getItemDto());
//
//        mvc.perform(post("/items")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .header("X-Sharer-User-Id", 1L)
//                        .content(objectMapper.writeValueAsString(getItemDto())))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.name").value("name"))
//                .andExpect(jsonPath("$.description").value("description"));
//    }
//
//    @Test
//    void updateTest() throws Exception {
//        ItemDto forUpdate = getItemDtoForUpdate();
//        when(itemService.update(anyLong(), any(), anyLong())).thenReturn(forUpdate);
//
//        mvc.perform(patch("/items/1")
//                        .content(objectMapper.writeValueAsString(forUpdate))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .header("X-Sharer-User-Id", 1L))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.name").value("updated"))
//                .andExpect(jsonPath("$.description").value("updated description"));
//    }
//
//    @Test
//    void update_whenItemNotFound() throws Exception {
//        ItemDto forUpdate = new ItemDto(1L, "updated", "updated description", true, null);
//        when(itemService.update(anyLong(), any(), anyLong())).thenThrow(new NotFoundException("Item not found"));
//
//        mvc.perform(patch("/items/1")
//                        .content(objectMapper.writeValueAsString(forUpdate))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .header("X-Sharer-User-Id", 1L))
//                .andExpect(status().isNotFound())
//                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
//                .andExpect(result -> assertEquals("Item not found",
//                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
//    }
//
//    @Test
//    void searchItemTest() throws Exception {
//        when(itemService.search(anyString(), any())).thenReturn(Collections.singletonList(getItemDto()));
//
//        mvc.perform(get("/items/search?text=дрель")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .header("X-Sharer-User-Id", 1L))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1L))
//                .andExpect(jsonPath("$[0].name").value("name"))
//                .andExpect(jsonPath("$[0].description").value("description"));
//    }
//
//    @Test
//    void addCommentTest() throws Exception {
//        User user = getUser();
//        CommentDto commentDto = new CommentDto();
//        commentDto.setId(1L);
//        commentDto.setText("Test");
//        commentDto.setAuthorName(user.getName());
//
//        when(itemService.createComment(anyLong(), any(), anyLong())).thenReturn(commentDto);
//
//        mvc.perform(post("/items/1/comment")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .header("X-Sharer-User-Id", 1L)
//                        .content(objectMapper.writeValueAsString(commentDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.text").value("Test"))
//                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()));
//    }
//}
