package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoRQ;
import ru.practicum.shareit.request.dto.ItemRequestDtoRS;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRepository itemRepository;
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDtoRS create(long userId, ItemRequestDtoRQ itemRequestDtoRQ) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        itemRequestDtoRQ.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = requestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDtoRQ, user));
        log.info("Request created");
        return ItemRequestMapper.toItemRequestDtoRS(itemRequest);
    }

    @Override
    public List<ItemRequestDtoRS> getInfo(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        List<ItemRequestDtoRS> responseList = requestRepository.findAllByRequesterId(userId).stream()
                .map(ItemRequestMapper::toItemRequestDtoRS)
                .collect(Collectors.toList());
        setItemsToRequests(responseList);
        return responseList;
    }

    @Override
    public ItemRequestDtoRS getInfo(long userId, long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        ItemRequest itemRequest = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Request not found"));
        List<ItemDto> items = itemRepository.findByItemRequestId(requestId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        ItemRequestDtoRS itemRequestDtoResponse = ItemRequestMapper.toItemRequestDtoRS(itemRequest);
        itemRequestDtoResponse.setItems(items);
        return itemRequestDtoResponse;
    }

    @Override
    public List<ItemRequestDtoRS> getRequests(long userId, int from, int size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<ItemRequestDtoRS> responseList = requestRepository.findAllPageable(userId, pageRequest).stream()
                .map(ItemRequestMapper::toItemRequestDtoRS)
                .collect(Collectors.toList());
        setItemsToRequests(responseList);
        return responseList;
    }

    private void setItemsToRequests(List<ItemRequestDtoRS> itemRequestDtoRS) {
        Map<Long, ItemRequestDtoRS> requests = itemRequestDtoRS.stream()
                .collect(Collectors.toMap(ItemRequestDtoRS::getId, x -> x, (a, b) -> b));
        List<Long> ids = requests.values().stream()
                .map(ItemRequestDtoRS::getId)
                .collect(Collectors.toList());
        List<ItemDto> itemDtos = itemRepository.searchByRequestsId(ids).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        itemDtos.forEach(itemDto -> requests.get(itemDto.getRequestId()).getItems().add(itemDto));
    }
}
