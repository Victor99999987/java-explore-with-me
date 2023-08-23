package ru.practicum.ewm.request.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.ConflictException;
import ru.practicum.ewm.common.NotFoundException;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.StateEvent;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.StateRequest;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    private static final Sort SORT_BY_ID = Sort.by(Sort.Direction.ASC, "id");

    public RequestServiceImpl(UserRepository userRepository, EventRepository eventRepository,
                              RequestRepository requestRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.requestRepository = requestRepository;
    }

    @Transactional
    @Override
    public List<ParticipationRequestDto> getAllRequestByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id %d не найден", userId)));

        List<Request> requests = requestRepository.findAllByRequester(user, SORT_BY_ID);
        return requests.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto addNewRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id %d не найден", userId)));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id %d не найдено", eventId)));

        if (requestRepository.existsByRequesterAndEvent(user, event)) {
            throw new ConflictException(String.format("Нельзя добавить повторный запрос. " +
                    "Пользователь с id %d уже учавствует в событии с id %d", userId, eventId));
        }

        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException(String.format("Инициатор события не может добавить запрос на участие в своём событии. " +
                    "Инициатор id %d, событие id %d ", userId, eventId));
        }

        if (event.getState() != StateEvent.PUBLISHED) {
            throw new ConflictException(String.format("Нельзя участвовать в неопубликованном событии. " +
                    "Пользователь id %d, событие id %d ", userId, eventId));
        }

        StateRequest stateRequest = StateRequest.PENDING;
        if(!event.isRequestModeration() || event.getParticipantLimit()==0){
            stateRequest = StateRequest.CONFIRMED;
        }

        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() <= requestRepository.countAllByEventAndStatus(event, StateRequest.CONFIRMED)) {
            throw new ConflictException(String.format("Достигнут лимит запросов на участие в событии id %d ", eventId));
        }

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .status(stateRequest)
                .build();

        request = requestRepository.save(request);
        return RequestMapper.toDto(request);
    }

    @Transactional
    @Override
    public ParticipationRequestDto patchRequestByUserIdAndRequestId(Long userId, Long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id %d не найден", userId)));

        Request request = requestRepository.findByIdAndRequester(requestId, user)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос на участие с id %d не найден", requestId)));

        request.setStatus(StateRequest.CANCELED);
        request = requestRepository.save(request);
        return RequestMapper.toDto(request);
    }

}
