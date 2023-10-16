package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoRQ;
import ru.practicum.shareit.request.dto.ItemRequestDtoRS;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    ItemRepository itemRepository;
    @Mock
    ItemRequestRepository requestRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    ItemRequestServiceImpl itemRequestService;
    User user = new User(
            1L,
            "name",
            "email@email.ru");
    ItemRequestDtoRQ itemRequestDto = new ItemRequestDtoRQ(
            1L,
            1L,
            "description",
            LocalDateTime.now());
    Item item = new Item(
            1L,
            "name",
            "description",
            true,
            user,
            null);

    @Test
    void create_whenUserFound_thenSaved() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        when(requestRepository.save(any())).thenReturn(itemRequest);
        ItemRequestDtoRS actual = itemRequestService.create(user.getId(), itemRequestDto);
        itemRequestDto.setCreated(actual.getCreated());

        verify(requestRepository, Mockito.times(1)).save(any());
    }

    @Test
    void create_whenUserNotFound() {
        when(userRepository.findById(anyLong())).thenThrow(new NotFoundException("User not found"));

        NotFoundException ex = assertThrows(NotFoundException.class, () -> itemRequestService.create(1L, itemRequestDto));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void getRequestsInfo_whenUserFound_thenReturnRequestsList() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        List<ItemRequestDtoRS> responseList = itemRequestService.getInfo(user.getId());
        assertTrue(responseList.isEmpty());
        verify(requestRepository).findAllByRequesterId(anyLong());
    }

    @Test
    void getRequestsInfo_whenUserNotFound() {
        when(userRepository.findById(anyLong())).thenThrow(new NotFoundException("User not found"));

        NotFoundException ex = assertThrows(NotFoundException.class, () -> itemRequestService.getInfo(1L));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void getRequestInfo_whenUserAndRequestFound_thenReturnRequestsList() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        item.setItemRequest(itemRequest);
        when(itemRepository.findByItemRequestId(anyLong())).thenReturn(Collections.singletonList(item));

        ItemRequestDtoRS responseRequest = itemRequestService.getInfo(user.getId(), itemRequestDto.getId());

        assertNotNull(responseRequest);
        verify(requestRepository).findById(anyLong());
        verify(itemRepository).findByItemRequestId(anyLong());
    }

    @Test
    void getRequestInfo_whenRequestNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.findById(anyLong())).thenThrow(new NotFoundException("Request not found"));

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                itemRequestService.getInfo(user.getId(), itemRequestDto.getId()));
        assertEquals("Request not found", ex.getMessage());
    }

    @Test
    void getRequestsListTest() {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        when(requestRepository.findAllPageable(anyLong(), any())).thenReturn(Collections.singletonList(itemRequest));

        List<ItemRequestDtoRS> items = itemRequestService.getRequests(1L, 0, 20);
        assertEquals(1, items.size());
        verify(requestRepository).findAllPageable(anyLong(), any());
    }
}