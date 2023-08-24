package ru.practicum.ewm.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.client.StatClient;
import ru.practicum.ewm.common.ConflictException;
import ru.practicum.ewm.common.NotFoundException;
import ru.practicum.ewm.common.Verify;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.mapper.LocationMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.model.StateEvent;
import ru.practicum.ewm.event.model.StateUpdateRequest;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.LocationRepository;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.CountRequest;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.StateRequest;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private static final LocalDateTime MIN_DATE = LocalDateTime.of(0, 1, 1, 0, 0);
    private static final LocalDateTime MAX_DATE = LocalDateTime.of(294276, 12, 31, 0, 0);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<List<ViewStatsDto>> TYPE_REFERENCE = new TypeReference<>() {
    };
    private static final Sort SORT_BY_ID = Sort.by(Sort.Direction.ASC, "id");
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final StatClient statClient;
    @Value("${app-name}")
    private String appName;

    @Transactional
    @Override
    public List<EventShortDto> getAllEventByUserId(Long userId, int from, int size) {
        User user = userService.getUserById(userId);
        Verify.verifyFromAndSize(from, size);
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, SORT_BY_ID);
        List<Event> events = eventRepository.findAllByInitiator(user, pageable);
        return events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto addNewEvent(Long userId, NewEventDto newEventDto) {
        User user = userService.getUserById(userId);
        Category category = categoryService.getCategoryById(newEventDto.getCategory());

        LocationDto locationDto = newEventDto.getLocation();
        Location location = locationRepository.findByLatAndLon(locationDto.getLat(), locationDto.getLon());
        if (location == null) {
            location = Location.builder()
                    .lat(locationDto.getLat())
                    .lon(locationDto.getLon())
                    .build();
            location = locationRepository.save(location);
        }

        LocalDateTime eventDate = newEventDto.getEventDate();
        if (LocalDateTime.now().plusHours(2).isAfter(eventDate)) {
            throw new IllegalArgumentException("Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента");
        }

        Event event = EventMapper.toEvent(newEventDto);
        event.setCategory(category);
        event.setInitiator(user);
        event.setLocation(location);
        event.setCreated(LocalDateTime.now());
        event.setPublished(LocalDateTime.now());
        event.setState(StateEvent.PENDING);
        event = eventRepository.save(event);

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        eventFullDto.setViews(0L);
        eventFullDto.setConfirmedRequests(0L);
        return eventFullDto;
    }


    @Transactional
    @Override
    public EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId) {
        User user = userService.getUserById(userId);
        Event event = getEventById(eventId);
        verifyEventByUser(event, user);

        return toEventFullDtoWithCounts(event);
    }

    @Transactional
    @Override
    public EventFullDto patchEventByUserIdAndEventId(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        User user = userService.getUserById(userId);
        Event event = getEventById(eventId);
        verifyEventByUser(event, user);
        if (event.getState() == StateEvent.PUBLISHED) {
            throw new ConflictException(String.format("Событие с id %d опубликовано, редактирование недоступно", eventId));
        }
        if (updateEventUserRequest.getEventDate() != null) {
            LocalDateTime eventDate = updateEventUserRequest.getEventDate();
            if (LocalDateTime.now().plusHours(2).isAfter(eventDate)) {
                throw new ConflictException("Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента");
            }
        }
        if (updateEventUserRequest.getAnnotation() != null) {
            if (updateEventUserRequest.getAnnotation().isBlank()) {
                throw new IllegalArgumentException("аннотация не должна состоять из пробелов");
            }
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(categoryService.getCategoryById(updateEventUserRequest.getCategory()));
        }
        if (updateEventUserRequest.getDescription() != null) {
            if (updateEventUserRequest.getDescription().isBlank()) {
                throw new IllegalArgumentException("описание не должно состоять из пробелов");
            }
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.getLocation() != null) {
            LocationDto locationDto = updateEventUserRequest.getLocation();
            Location location = locationRepository.findByLatAndLon(locationDto.getLat(), locationDto.getLon());
            if (location == null) {
                location = LocationMapper.toLocation(locationDto);
                location = locationRepository.save(location);
            }
            event.setLocation(location);
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case CANCEL_REVIEW:
                    event.setState(StateEvent.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    event.setState(StateEvent.PENDING);
                    break;
            }
        }
        if (updateEventUserRequest.getTitle() != null) {
            if (updateEventUserRequest.getTitle().isBlank()) {
                throw new IllegalArgumentException("заголовок не должен состоять из пробелов");
            }
            event.setTitle(updateEventUserRequest.getTitle());
        }

        event = eventRepository.save(event);

        return toEventFullDtoWithCounts(event);
    }

    @Transactional
    @Override
    public List<ParticipationRequestDto> getRequestsByUserIdAndEventId(Long userId, Long eventId) {
        User user = userService.getUserById(userId);
        Event event = getEventById(eventId);
        verifyEventByUser(event, user);
        List<Request> requests = requestRepository.findAllByEvent(event, SORT_BY_ID);

        return requests.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getPiblicEventById(Long id, HttpServletRequest request) {
        Event event = getEventById(id);
        if (event.getState() != StateEvent.PUBLISHED) {
            throw new NotFoundException(String.format("Событие с id %d недоступно для просмотра", id));
        }

        sendStat(request);

        return toEventFullDtoWithCounts(event);
    }

    private Map<String, Long> getViews(List<Event> events) {
        LocalDateTime start;
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = null;

        if (events == null || events.size() == 0) {
            start = MIN_DATE;
        } else {
            start = events.get(0).getPublished();
            uris = new ArrayList<>();
            for (Event event : events) {
                if (event.getPublished().isBefore(start)) {
                    start = event.getPublished();
                }
                uris.add("/events/" + event.getId());
            }
        }

        ResponseEntity<Object> response = statClient.getStats(start, end, uris, true);

        Map<String, Long> result = new HashMap<>();

        if (response.getStatusCode() == HttpStatus.OK) {
            List<ViewStatsDto> stats = MAPPER.convertValue(response.getBody(), TYPE_REFERENCE);

            for (ViewStatsDto viewStatsDto : stats) {
                result.put(viewStatsDto.getUri(), viewStatsDto.getHits());
            }
        }
        return result;
    }

    private void sendStat(HttpServletRequest request) {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(appName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
        if (endpointHitDto.getIp().equals("0:0:0:0:0:0:0:1")) {
            endpointHitDto.setIp("127.0.0.1");
        }
        statClient.sendHit(endpointHitDto);
    }

    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id %d не найдено", eventId)));
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult patchRequestStatusesByUserIdAndEventId(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        User user = userService.getUserById(userId);
        Event event = getEventById(eventId);
        verifyEventByUser(event, user);
        List<Request> requests = requestRepository.findAllByEventAndIdIn(event, eventRequestStatusUpdateRequest.getRequestIds());

        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();

        Long countConfirmedRequests = requestRepository.countAllByEventAndStatus(event, StateRequest.CONFIRMED);

        if (event.isRequestModeration() && event.getParticipantLimit() > 0 && countConfirmedRequests >= event.getParticipantLimit()) {
            throw new ConflictException("Достигнут лимит по заявкам на данное событие");
        }

        if (event.isRequestModeration() && event.getParticipantLimit() > 0) {
            for (Request request : requests) {
                if (request.getStatus() != StateRequest.PENDING) {
                    throw new ConflictException("статус можно изменить только у заявок, находящихся в состоянии ожидания");
                }
                if (countConfirmedRequests < event.getParticipantLimit() &&
                        eventRequestStatusUpdateRequest.getStatus() == StateUpdateRequest.CONFIRMED) {
                    request.setStatus(StateRequest.CONFIRMED);
                    confirmedRequests.add(request);
                    countConfirmedRequests++;
                } else {
                    request.setStatus(StateRequest.REJECTED);
                    rejectedRequests.add(request);
                }
            }
            requestRepository.saveAll(requests);
        }

        List<ParticipationRequestDto> confirmedRequestDtos = confirmedRequests.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());

        List<ParticipationRequestDto> rejectedRequestDtos = rejectedRequests.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequestDtos)
                .rejectedRequests(rejectedRequestDtos)
                .build();
    }

    @Transactional
    @Override
    public List<EventFullDto> getAllEventsByFilter(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, boolean onlyAvailable, String sort, int from, int size, HttpServletRequest httpServletRequest) {
        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new IllegalArgumentException("Дата окончания периода раньше даты начала.");
            }
        }

        if (rangeStart == null) {
            rangeStart = MIN_DATE;
        }
        if (rangeEnd == null) {
            rangeEnd = MAX_DATE;
        }

        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, SORT_BY_ID);

        List<Event> events = eventRepository.findAllPublicByFilter(text, categories, paid, rangeStart, rangeEnd, pageable);

        sendStat(httpServletRequest);

        return toEventFullDtoWithCounts(events);
    }

    @Transactional
    @Override
    public List<EventFullDto> getAllEventsByAdminFilter(List<Long> users, List<StateEvent> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new IllegalArgumentException("Дата окончания периода раньше даты начала.");
            }
        }

        if (rangeStart == null) {
            rangeStart = MIN_DATE;
        }
        if (rangeEnd == null) {
            rangeEnd = MAX_DATE;
        }

        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, SORT_BY_ID);

        List<Event> events = eventRepository.findAllByAdminFilter(users, states, categories, rangeStart, rangeEnd, pageable);

        return toEventFullDtoWithCounts(events);
    }

    @Transactional
    @Override
    public EventFullDto patchEventById(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = getEventById(eventId);

        if (updateEventAdminRequest.getEventDate() != null && updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Новая дата события не может быть в прошлом");
        }

        if (updateEventAdminRequest.getAnnotation() != null) {
            if (updateEventAdminRequest.getAnnotation().isBlank()) {
                throw new IllegalArgumentException("аннотация не может состоять из пробелов");
            }
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(categoryService.getCategoryById(updateEventAdminRequest.getCategory()));
        }
        if (updateEventAdminRequest.getDescription() != null) {
            if (updateEventAdminRequest.getDescription().isBlank()) {
                throw new IllegalArgumentException("описание не может состоять из пробелов");
            }
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            LocationDto locationDto = updateEventAdminRequest.getLocation();
            Location location = locationRepository.findByLatAndLon(locationDto.getLat(), locationDto.getLon());
            if (location == null) {
                location = LocationMapper.toLocation(locationDto);
                location = locationRepository.save(location);
            }
            event.setLocation(location);
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            switch (updateEventAdminRequest.getStateAction()) {
                case REJECT_EVENT:
                    if (event.getState() == StateEvent.PUBLISHED) {
                        throw new ConflictException("Событие уже опубликовано, отклонить не получится");
                    }
                    event.setState(StateEvent.CANCELED);
                    break;
                case PUBLISH_EVENT:
                    if (event.getState() != StateEvent.PENDING) {
                        throw new ConflictException("Событие можно публиковать, только если оно в состоянии ожидания публикации");
                    }
                    event.setState(StateEvent.PUBLISHED);
                    break;
            }
        }
        if (updateEventAdminRequest.getTitle() != null) {
            if (updateEventAdminRequest.getTitle().isBlank()) {
                throw new IllegalArgumentException("заголовок не может состоять из пробелов");
            }
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        event = eventRepository.save(event);

        return toEventFullDtoWithCounts(event);
    }

    private EventFullDto toEventFullDtoWithCounts(Event event) {
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);

        Long confirmedRequests = requestRepository.countAllByEventAndStatus(event, StateRequest.CONFIRMED);
        eventFullDto.setConfirmedRequests(confirmedRequests);

        Map<String, Long> stats = getViews(List.of(event));

        Long views = stats.getOrDefault("/events/" + eventFullDto.getId(), 0L);
        eventFullDto.setViews(views);

        return eventFullDto;
    }

    private List<EventFullDto> toEventFullDtoWithCounts(List<Event> events) {
        //List<Request> requests = requestRepository.findAllByEventInAndStatus(events, StateRequest.CONFIRMED);
        List<CountRequest> countRequests = requestRepository.findAllCountsByConfirmedEventIn(events);
        Map<Long, Long> counts = countRequests.stream()
                .collect(Collectors.toMap((entry) -> entry.getEventId(), (entry) -> entry.getCount()));

        List<EventFullDto> eventFullDtos = events.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());

        Map<String, Long> stats = getViews(events);

        for (EventFullDto eventFullDto : eventFullDtos) {
            //Long confirmedRequests = requests.stream()
            //        .filter(r -> Objects.equals(r.getEvent().getId(), eventFullDto.getId()))
            //        .count();
            Long confirmedRequests = counts.getOrDefault(eventFullDto.getId(), 0L);
            eventFullDto.setConfirmedRequests(confirmedRequests);
            Long views = stats.getOrDefault("/events/" + eventFullDto.getId(), 0L);
            eventFullDto.setViews(views);
        }
        return eventFullDtos;
    }

    private void verifyEventByUser(Event event, User user) {
        if (!Objects.equals(event.getInitiator().getId(), user.getId())) {
            throw new NotFoundException(String.format("Пользователь с id %d не является инициатором события с id %d",
                    user.getId(), event.getId()));
        }
    }

}
