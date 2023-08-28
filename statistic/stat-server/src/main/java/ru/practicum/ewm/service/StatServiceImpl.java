package ru.practicum.ewm.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.mapper.StatMapper;
import ru.practicum.ewm.model.Stat;
import ru.practicum.ewm.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;

    public StatServiceImpl(StatRepository statRepository) {
        this.statRepository = statRepository;
    }

    @Override
    public void addHit(EndpointHitDto endpointHitDto) {
        Stat stat = StatMapper.toStat(endpointHitDto);
        statRepository.save(stat);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Дата начала периода не может быть позже конца периода");
        }
        if (uris == null) {
            if (unique) {
                return statRepository.getStatsByUniqueIp(start, end);
            }
            return statRepository.getStatsByNonUniqueIp(start, end);
        } else {
            if (unique) {
                return statRepository.getStatsByUrisAndUniqueIp(start, end, uris);
            }
            return statRepository.getStatsByUrisAndNonUniqueIp(start, end, uris);
        }
    }
}
