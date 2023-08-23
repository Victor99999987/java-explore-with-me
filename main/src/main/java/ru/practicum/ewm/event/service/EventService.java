package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.StateEvent;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventShortDto> getAllEventByUserId(Long userId, int from, int size);

    EventFullDto addNewEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId);

    EventFullDto patchEventByUserIdAndEventId(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getRequestsByUserIdAndEventId(Long userId, Long eventId);

    EventFullDto getPiblicEventById(Long id, HttpServletRequest request);

    Event getEventById(Long eventId);

    EventRequestStatusUpdateResult patchRequestStatusesByUserIdAndEventId(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<EventFullDto> getAllEventsByFilter(String text, List<Long> categories, boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, boolean onlyAvailable, String sort, int from, int size,HttpServletRequest request);

    List<EventFullDto> getAllEventsByAdminFilter(List<Long> users, List<StateEvent> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    EventFullDto patchEventById(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
