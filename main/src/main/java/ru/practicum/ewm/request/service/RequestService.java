package ru.practicum.ewm.request.service;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getAllRequestByUserId(Long userId);

    ParticipationRequestDto addNewRequest(Long userId, Long eventId);

    ParticipationRequestDto patchRequestByUserIdAndRequestId(Long userId, Long requestId);
}
