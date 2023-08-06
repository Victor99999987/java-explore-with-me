package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.model.Stat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<Stat, Long> {

    @Query("select new ru.practicum.ewm.dto.ViewStatsDto(st.app, st.uri, count(distinct st.ip)) " +
            "from Stat as st " +
            "where st.created between ?1 and ?2 " +
            "group by st.app, st.uri " +
            "order by count(distinct st.ip) desc")
    List<ViewStatsDto> getStatsByUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.ewm.dto.ViewStatsDto(st.app, st.uri, count(st.ip)) " +
            "from Stat as st " +
            "where st.created between ?1 and ?2 " +
            "group by st.app, st.uri " +
            "order by count(st.ip) desc")
    List<ViewStatsDto> getStatsByNonUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.ewm.dto.ViewStatsDto(st.app, st.uri, count(distinct st.ip)) " +
            "from Stat as st " +
            "where st.created between ?1 and ?2 " +
            "and st.uri in ?3 " +
            "group by st.app, st.uri " +
            "order by count(distinct st.ip) desc")
    List<ViewStatsDto> getStatsByUrisAndUniqueIp(LocalDateTime start, LocalDateTime end, String[] uris);

    @Query("select new ru.practicum.ewm.dto.ViewStatsDto(st.app, st.uri, count(st.ip)) " +
            "from Stat as st " +
            "where st.created between ?1 and ?2 " +
            "and st.uri in ?3 " +
            "group by st.app, st.uri " +
            "order by count(st.ip) desc")
    List<ViewStatsDto> getStatsByUrisAndNonUniqueIp(LocalDateTime start, LocalDateTime end, String[] uris);
}
