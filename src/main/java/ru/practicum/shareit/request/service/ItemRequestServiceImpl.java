package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;


@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Пользователь " +
                "с id = %s отсутствует в БД. Выполнить операцию невозможно!", id)));
    }

    private ItemRequest getItemRequestById(long id) {
        return itemRequestRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Запрос " +
                "с id = %s отсутствует в БД. Выполнить операцию невозможно!", id)));
    }

    @Override
    public ItemRequestDtoResponse create(ItemRequestDtoRequest requestDto, long userId) {
        User requestor = getUserById(userId);
        LocalDateTime currentMoment = LocalDateTime.now();

        ItemRequest dataRequest = ItemRequestMapper.toItemRequest(requestDto, requestor, currentMoment);
        ItemRequest request = itemRequestRepository.save(dataRequest);
        log.info("Данные запроса необходимой вещи добавлены в БД: {}.", request);

        ItemRequestDtoResponse responseItemRequestDto = ItemRequestMapper.toResponseItemRequestDto(request,
                UserMapper.toResponseUserDto(requestor));
        log.info("Новая вещь создана: {}.", responseItemRequestDto);
        return responseItemRequestDto;
    }

    @Override
    public ItemRequestDtoFullResponse getById(long requestId, long userId) {
        getUserById(userId);
        ItemRequest itemRequest = getItemRequestById(requestId);
        log.info("Запрос найден в БД: {}.", itemRequest);

        List<Item> oneRequestItems =  itemRepository.findByItemRequestId(requestId);
        ItemRequestDtoFullResponse requestDto = ItemRequestMapper.toFullResponseItemRequestDto(itemRequest);
        setItemList(requestDto, oneRequestItems);
        log.info("Все данные запроса вещи получены: {}.", requestDto);
        return requestDto;
    }

    @Override
    public List<ItemRequestDtoFullResponse> getOwnItemRequests(long requestorId) {
        getUserById(requestorId);

        if (!itemRequestRepository.existsAllByRequestorId(requestorId)) {
            log.info("У пользователя с id = {} отсутствуют запросы на вещи.", requestorId);
            return new ArrayList<>();
        } else {
            List<ItemRequest> requestList = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(requestorId);
            List<Long> requestIdList = requestList.stream().map(ItemRequest::getId).collect(toList());
            Map<ItemRequest, List<Item>> allItems = itemRepository.findAllByItemRequestIdIn(requestIdList)
                    .stream()
                    .collect(groupingBy(Item::getItemRequest, toList()));
            log.info("Из БД получены запросы пользователя и вещи, предложенные в соответствии с ними другими " +
                    "пользователями.");

            log.info("Формируем итоговый список запросов.");
            return requestList
                    .stream()
                    .map((ItemRequest request) -> {
                        ItemRequestDtoFullResponse requestDto = ItemRequestMapper.toFullResponseItemRequestDto(request);
                        return setItemList(requestDto, allItems.get(request));
                    })
                    .collect(toList());
        }
    }

    public List<ItemRequestDtoFullResponse> getAll(long userId, int from, int size) {
        getUserById(userId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        Page<ItemRequest> requestList = itemRequestRepository.findAllByRequestorIdNot(userId, pageable);
        log.info("Из БД получены все запросы пользователей.");
        if (!requestList.hasContent()) {
            log.info("Запросы на вещи отсутствуют.");
            return new ArrayList<>();
        } else {
            List<Item> items = itemRepository.findByItemRequestIdIsNotNull();
            Map<ItemRequest, List<Item>> allItems = items
                    .stream()
                    .collect(groupingBy(Item::getItemRequest, toList()));
            log.info("Из БД получены запросы пользователей и вещи, предложенные в соответствии с ними другими " +
                    "пользователями.");

            log.info("Формируем итоговый список запросов.");
            return requestList
                    .stream()
                    .map((ItemRequest request) -> {
                        ItemRequestDtoFullResponse requestDto = ItemRequestMapper.toFullResponseItemRequestDto(request);
                        return setItemList(requestDto, allItems.get(request));
                    })
                    .collect(toList());
        }
    }

    private ItemRequestDtoFullResponse setItemList(ItemRequestDtoFullResponse requestDto, List<Item> oneRequestItems) {
        List<ItemDtoResponse> items;

        if (oneRequestItems != null) {
            items = oneRequestItems.stream()
                    .map(ItemMapper::toResponseItemDto)
                    .collect(toList());
        } else {
            items = new ArrayList<>();
        }
        requestDto.setItems(items);
        return requestDto;
    }

}
