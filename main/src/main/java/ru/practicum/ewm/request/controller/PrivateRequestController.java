package ru.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PrivateRequestController {
    private final RequestService requestService;

    @GetMapping("/users/{userId}/requests")
    List<ParticipationRequestDto> getAllRequestByUserId(@PathVariable Long userId) {
        log.info("Получен запрос на эндпоинт GET /users/{}/requests", userId);
        return requestService.getAllRequestByUserId(userId);
    }

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    ParticipationRequestDto addNewRequest(@PathVariable Long userId,
                                          @RequestParam Long eventId) {
        log.info("Получен запрос на эндпоинт POST /users/{}/requests", userId);
        return requestService.addNewRequest(userId, eventId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    ParticipationRequestDto patchRequestByUserIdAndRequestId(@PathVariable Long userId,
                                                             @PathVariable Long requestId) {
        log.info("Получен запрос на эндпоинт PATCH /users/{}/requests/{}/cancel", userId, requestId);
        return requestService.patchRequestByUserIdAndRequestId(userId, requestId);
    }

}
