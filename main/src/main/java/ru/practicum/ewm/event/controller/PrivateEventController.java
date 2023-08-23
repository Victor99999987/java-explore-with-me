package ru.practicum.ewm.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@Slf4j
public class PrivateEventController {
    private final EventService eventService;

    public PrivateEventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/users/{userId}/events")
    List<EventShortDto> getAllEventByUserId(@PathVariable Long userId,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "10") int size) {
        return eventService.getAllEventByUserId(userId, from, size);
    }

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    EventFullDto addNewEvent(@PathVariable Long userId,
                             @Valid @RequestBody NewEventDto newEventDto) {
        return eventService.addNewEvent(userId, newEventDto);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    EventFullDto getEventByUserIdAndEventId(@PathVariable Long userId,
                                            @PathVariable Long eventId) {
        return eventService.getEventByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    EventFullDto patchEventByUserIdAndEventId(@PathVariable Long userId,
                                              @PathVariable Long eventId,
                                              @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return eventService.patchEventByUserIdAndEventId(userId, eventId, updateEventUserRequest);
    }


    @GetMapping("/users/{userId}/events/{eventId}/requests")
    List<ParticipationRequestDto> getRequestsByUserIdAndEventId(@PathVariable Long userId,
                                                                @PathVariable Long eventId) {
        return eventService.getRequestsByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    EventRequestStatusUpdateResult patchRequestStatusesByUserIdAndEventId(@PathVariable Long userId,
                                                                          @PathVariable Long eventId,
                                                                          @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return eventService.patchRequestStatusesByUserIdAndEventId(userId, eventId, eventRequestStatusUpdateRequest);
    }


}
