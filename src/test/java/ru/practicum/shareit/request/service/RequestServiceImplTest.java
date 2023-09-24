package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestInputDto;
import ru.practicum.shareit.request.dto.RequestOutputDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository requestRepository;

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    @Test
    void shouldCreateRequest() {
        LocalDateTime timeCreated = LocalDateTime.now();
        RequestInputDto requestInputDto = new RequestInputDto(
                " descriptionOfRequest1"
        );

        ItemRequest request = new ItemRequest(1L,
                " descriptionOfRequest1",
                1L,
                timeCreated);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(requestRepository.save(any())).thenReturn(request);

        RequestOutputDto requestOutputDto = requestService.createRequest(requestInputDto, 1);

        assertEquals(request.getId(), requestOutputDto.getId());
        assertEquals(request.getDescription(), requestOutputDto.getDescription());
        assertEquals(request.getCreated(), requestOutputDto.getCreated());
    }

    @Test
    void shouldGetRequestsByAuthor() {

        ItemRequest request1 = new ItemRequest(1L,
                " descriptionOfRequest1",
                1L,
                LocalDateTime.now().minusDays(2));
        ItemRequest request2 = new ItemRequest(2L,
                " descriptionOfRequest2",
                1L,
                LocalDateTime.now().minusDays(1));
        List<ItemRequest> requests = List.of(request1, request2);

        Item item1 = new Item(1L, "item1", "description Item1", true, 1L, request1);
        Item item2 = new Item(2L, "item2", "description Item2", true, 1L, request2);
        List<Item> items = List.of(item1, item2);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(requestRepository.findAllByRequester(anyLong())).thenReturn(requests);
        when(itemRepository.findByRequestIn(anyList(), any())).thenReturn(items);

        List<RequestOutputDto> requestOutputDtosAfter = requestService.getRequestsByAuthor(1L);

        assertEquals(requestOutputDtosAfter.size(), requests.size());
        assertEquals(requestOutputDtosAfter.get(0).getId(), requests.get(0).getId());
        assertEquals(requestOutputDtosAfter.get(0).getDescription(), requests.get(0).getDescription());
        assertEquals(requestOutputDtosAfter.get(0).getCreated(), requests.get(0).getCreated());
    }

    @Test
    void shouldGetAllRequests() {
        ItemRequest request1 = new ItemRequest(1L,
                " descriptionOfRequest1",
                1L,
                LocalDateTime.now().minusDays(2));
        Item item1 = new Item(1L, "item1", "description Item1", true, 1L, request1);
        Item item2 = new Item(2L, "item2", "description Item2", true, 1L, request1);
        List<Item> items = List.of(item1, item2);
        when(requestRepository.findRequestsByRequesterNotOrderByCreatedDesc(anyLong(), any()))
                .thenReturn(new PageImpl<ItemRequest>(Collections.singletonList(request1)));
        when(itemRepository.findByRequestIn(anyList(), any())).thenReturn(items);

        List<RequestOutputDto> requestOutputDtosAfter = requestService.getAllRequests(1L, 0, 1);

        assertEquals(requestOutputDtosAfter.size(), 1);
        assertEquals(requestOutputDtosAfter.get(0).getId(), request1.getId());
        assertEquals(requestOutputDtosAfter.get(0).getRequestor(), request1.getRequester());
        assertEquals(requestOutputDtosAfter.get(0).getDescription(), request1.getDescription());
        assertEquals(requestOutputDtosAfter.get(0).getCreated(), request1.getCreated());
        assertEquals(requestOutputDtosAfter.get(0).getItems().size(), items.size());
    }

    @Test
    void shouldGetRequestById() {
        ItemRequest request1 = new ItemRequest(1L,
                " descriptionOfRequest1",
                1L,
                LocalDateTime.now().minusDays(2));
        Item item1 = new Item(1L, "item1", "description Item1", true, 1L, request1);
        Item item2 = new Item(2L, "item2", "description Item2", true, 1L, request1);
        List<Item> items = List.of(item1, item2);
        when(requestRepository.findRequestById(anyLong()))
                .thenReturn(java.util.Optional.of(request1));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findByRequestIn(anyList(), any())).thenReturn(items);

        RequestOutputDto requestOutputDtoAfter = requestService.getRequestById(1L, 1L);
        assertEquals(requestOutputDtoAfter.getId(), request1.getId());
        assertEquals(requestOutputDtoAfter.getDescription(), request1.getDescription());
        assertEquals(requestOutputDtoAfter.getDescription(), request1.getDescription());
        assertEquals(requestOutputDtoAfter.getItems().size(),  items.size());
    }
}
