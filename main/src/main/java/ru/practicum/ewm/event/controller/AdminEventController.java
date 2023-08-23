package ru.practicum.ewm.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.model.StateEvent;
import ru.practicum.ewm.event.service.EventService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@Slf4j
public class AdminEventController {
    private final EventService eventService;

    public AdminEventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/admin/events")
    List<EventFullDto> getAllEventsByAdminFilter(@RequestParam(required = false) List<Long> users,
                                                 @RequestParam(required = false) List<StateEvent> states,
                                                 @RequestParam(required = false) List<Long> categories,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                 @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "10") int size) {
        return eventService.getAllEventsByAdminFilter(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/admin/events/{eventId}")
    EventFullDto patchEventById(@PathVariable Long eventId,
                                @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        return eventService.patchEventById(eventId, updateEventAdminRequest);
    }

}