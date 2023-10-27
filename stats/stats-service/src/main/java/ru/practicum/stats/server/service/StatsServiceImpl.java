package ru.practicum.stats.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.entity.HitMapper;
import ru.practicum.stats.server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository repository;
    private final HitMapper mapper;

    @Override
    @Transactional
    public HitDto saveHit(HitDto hitDto) {
        repository.save(mapper.mapToEntity(hitDto));
        log.info("Hit {} создан.", hitDto);
        return hitDto;
    }

    @Override
    public List<ViewStatsDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start.isAfter(end)) {
            log.info("Некорректное время старта");
            throw new IllegalStateException("Некорректное время старта");
        }
        log.info("Получена stats с параметрами: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return repository.getStatsUniqueIp(start, end);
            } else {
                return repository.getStats(start, end);
            }
        } else {
            if (unique) {
                return repository.getStatsUniqueIpForUris(start, end, uris);
            } else {
                return repository.getStatsForUris(start, end, uris);
            }
        }
    }

}
