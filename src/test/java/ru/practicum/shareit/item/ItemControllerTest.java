package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDtoShort;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.utils.Constants.X_SHARER_USER_ID;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    private ItemServiceImpl itemService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private final ItemDtoOut itemDtoOut = new ItemDtoOut(
            1,
            "item",
            "cool item",
            true,
            new UserDtoShort(1L, "User"));
    private final ItemDtoOut itemBlankName = new ItemDtoOut(
            1,
            "",
            "cool item",
            true,
            new UserDtoShort(1L, "User"));

    @Test
    void saveNewItem() throws Exception {
        when(itemService.saveNewItem(any(), anyLong())).thenReturn(itemDtoOut);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoOut))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDtoOut)))
                .andExpect(jsonPath("$.id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOut.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDtoOut.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDtoOut.getAvailable()), Boolean.class));
    }

    @Test
    void saveNewItem_whenBlankName_thenThrownException() throws Exception {
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemBlankName))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(anyLong(), any(), anyLong())).thenReturn(itemDtoOut);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDtoOut))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDtoOut)))
                .andExpect(jsonPath("$.id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOut.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDtoOut.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDtoOut.getAvailable()), Boolean.class));
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDtoOut);

        mvc.perform(get("/items/1")
                        .content(mapper.writeValueAsString(itemDtoOut))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOut.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoOut.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoOut.getAvailable())));
    }

    @Test
    void getItemsByOwner() throws Exception {
        when(itemService.getItemsByOwner(anyInt(), anyInt(), anyLong())).thenReturn(List.of(itemDtoOut));

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDtoOut))));
    }

    @Test
    void getFilmBySearch() throws Exception {
        when(itemService.getItemBySearch(any(), any(), any())).thenReturn(List.of(itemDtoOut));

        mvc.perform(get("/items/search?text=a&from=0&size=4")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDtoOut))));
    }

    @Test
    void saveNewComment() throws Exception {
        final CommentDtoOut commentDtoOut = new CommentDtoOut(1L, "comment", "user",
                LocalDateTime.now());

        when(itemService.saveNewComment(anyLong(), any(), anyLong())).thenReturn(commentDtoOut);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDtoOut))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDtoOut)))
                .andExpect(jsonPath("$.id", is(commentDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDtoOut.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDtoOut.getAuthorName())));
    }
}