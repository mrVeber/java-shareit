package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.RequestInputDto;
import ru.practicum.shareit.request.dto.RequestOutputDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemRequestMapperTest {
    @Test
    void toRequestOutputDtoTest() {
        ItemRequest request = new ItemRequest(1l,
                " descriptionOfRequest1",
                1l,
                LocalDateTime.now());
        RequestOutputDto requestOutputDto = ItemRequestMapper.toRequestOutputDto(request);

        assertEquals(request.getId(), requestOutputDto.getId());
        assertEquals(request.getDescription(), requestOutputDto.getDescription());
        assertEquals(request.getDescription(), requestOutputDto.getDescription());
        assertEquals(request.getCreated(), requestOutputDto.getCreated());
    }

    @Test
    void toRequestTest() {
        RequestInputDto requestInputDto = new RequestInputDto(
                " descriptionOfRequest1");
        ItemRequest request = ItemRequestMapper.toRequest(requestInputDto, 1L);
        assertEquals(request.getDescription(), requestInputDto.getDescription());
    }
}
