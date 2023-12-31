package ru.practicum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.entity.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Hit, Long> {

    @Query("SELECT new ru.practicum.stats.dto.ViewStatsDto(h.app, h.uri, COUNT(h.ip)) " +
            "FROM Hit as h " +
            "WHERE h.timestamp between ?1 AND ?2 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.stats.dto.ViewStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM Hit as h " +
            "WHERE h.timestamp between ?1 AND ?2 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStatsDto> getStatsUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.stats.dto.ViewStatsDto(h.app, h.uri,COUNT(h.ip)) " +
            "FROM Hit as h " +
            "WHERE h.timestamp between ?1 AND ?2 AND h.uri IN(?3)" +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<ViewStatsDto> getStatsForUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.stats.dto.ViewStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM Hit as h " +
            "WHERE h.timestamp between ?1 AND ?2 AND h.uri IN(?3)" +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStatsDto> getStatsUniqueIpForUris(LocalDateTime start, LocalDateTime end, List<String> uris);

}