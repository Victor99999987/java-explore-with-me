package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@Slf4j
@RequiredArgsConstructor
public class PrivateEventController {
    private final EventService eventService;

    @GetMapping("/users/{userId}/events")
    List<EventShortDto> getAllEventByUserId(@PathVariable Long userId,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получен запрос на эндпоинт GET /users/{}/events", userId);
        return eventService.getAllEventByUserId(userId, from, size);
    }

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    EventFullDto addNewEvent(@PathVariable Long userId,
                             @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Получен запрос на эндпоинт POST /users/{}/events", userId);
        return eventService.addNewEvent(userId, newEventDto);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    EventFullDto getEventByUserIdAndEventId(@PathVariable Long userId,
                                            @PathVariable Long eventId) {
        log.info("Получен запрос на эндпоинт GET /users/{}/events/{}", userId, eventId);
        return eventService.getEventByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    EventFullDto patchEventByUserIdAndEventId(@PathVariable Long userId,
                                              @PathVariable Long eventId,
                                              @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("Получен запрос на эндпоинт PATCH /users/{}/events/{}", userId, eventId);
        return eventService.patchEventByUserIdAndEventId(userId, eventId, updateEventUserRequest);
    }


    @GetMapping("/users/{userId}/events/{eventId}/requests")
    List<ParticipationRequestDto> getRequestsByUserIdAndEventId(@PathVariable Long userId,
                                                                @PathVariable Long eventId) {
        log.info("Получен запрос на эндпоинт GET /users/{}/events/{}/requests", userId, eventId);
        return eventService.getRequestsByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    EventRequestStatusUpdateResult patchRequestStatusesByUserIdAndEventId(@PathVariable Long userId,
                                                                          @PathVariable Long eventId,
                                                                          @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("Получен запрос на эндпоинт PATCH /users/{}/events/{}/requests", userId, eventId);
        return eventService.patchRequestStatusesByUserIdAndEventId(userId, eventId, eventRequestStatusUpdateRequest);
    }

}
