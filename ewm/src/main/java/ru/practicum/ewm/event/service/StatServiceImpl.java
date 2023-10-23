package ru.practicum.ewm.event.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventConfirmedRequests;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@ComponentScan("ru.practicum.stats")
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatsClient statClient;
    private final RequestRepository requestRepository;


    @Override
    public void hit(String uri, String ip) {
        HitDto hit = buildHit(uri, ip);
        log.info("Добавлен Hit {}", hit);
        statClient.saveHit(hit);
    }

    private HitDto buildHit(String uri, String ip) {
        return HitDto.builder()
                .app("ewm-main")
                .uri(uri)
                .timestamp(LocalDateTime.now())
                .ip(ip)
                .build();
    }

    @Override
    public Map<Long, Long> getViews(List<Event> events) {
        Map<Long, Long> views = new HashMap<>();

        List<Event> publishedEvents = events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .collect(Collectors.toList());

        Optional<LocalDateTime> minPublished = publishedEvents.stream()
                .map(Event::getPublishedOn)
                .min(LocalDateTime::compareTo);

        if (minPublished.isPresent()) {
            LocalDateTime start = minPublished.get();
            LocalDateTime end = LocalDateTime.now();
            List<String> uris = publishedEvents.stream()
                    .map(e -> "/events/" + e.getId())
                    .collect(Collectors.toList());

            List<ViewStatsDto> stats = statClient.getAllStats(start, end, uris, true);
            stats.forEach(s -> {
                Long eventId = Long.parseLong(s.getUri().substring(s.getUri().lastIndexOf("/") + 1));
                views.put(eventId, s.getHits());
            });
        }

        return views;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Long> getConfirmedRequests(List<Event> events) {
        List<Long> publishedIds = events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .map(Event::getId)
                .collect(Collectors.toList());

        return requestRepository.getConfirmedRequests(publishedIds).stream()
                .collect(Collectors.toMap(
                                EventConfirmedRequests::getEventId,
                                EventConfirmedRequests::getConfirmed
                        )
                );


    }

}
