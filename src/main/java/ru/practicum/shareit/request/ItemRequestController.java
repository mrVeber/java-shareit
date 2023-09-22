package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.RequestInputDto;
import ru.practicum.shareit.request.dto.RequestOutputDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.validators.Create;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.utils.Constants.X_SHARER_USER_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public RequestOutputDto createRequest(@Validated(Create.class) @RequestBody RequestInputDto requestInputDto,
                                          @RequestHeader(X_SHARER_USER_ID) long userId) {

        log.info(" RequestController -  createRequest(). Создан {}", requestInputDto.toString());
        return requestService.createRequest(requestInputDto, userId);
    }

    @GetMapping
    public List<RequestOutputDto> getRequestsByAuthor(@RequestHeader(X_SHARER_USER_ID) long userId) {

        List<RequestOutputDto> requestOutputDtos = requestService.getRequestsByAuthor(userId);
        log.info(" RequestController -  getRequestsByAuthor(). Возвращен список из {} запросов", requestOutputDtos.size());
        return requestOutputDtos;
    }

    @GetMapping("/all")
    public List<RequestOutputDto> getAllRequests(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                 @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                                 @RequestParam(name = "size", defaultValue = "10") @Min(1) @Max(100) Integer size) {
        return requestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestOutputDto getRequestById(@RequestHeader(X_SHARER_USER_ID) long userId,
                                           @PathVariable(name = "requestId") Long requestId) {
        RequestOutputDto requestOutputDto = requestService.getRequestById(requestId, userId);
        log.info(" RequestController -  getRequestById(). Возвращен {}", requestOutputDto.toString());
        return requestOutputDto;
    }
}