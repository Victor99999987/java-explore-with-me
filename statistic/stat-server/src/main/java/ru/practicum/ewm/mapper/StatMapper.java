package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.model.Stat;

@UtilityClass
public class StatMapper {
    public Stat toStat(EndpointHitDto endpointHitDto) {
        return Stat.builder()
                .id(endpointHitDto.getId())
                .app(endpointHitDto.getApp())
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .created(endpointHitDto.getTimestamp())
                .build();
    }

}
