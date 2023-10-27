package ru.practicum.stats.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
public class StatsController {

    private final StatsService service;
    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @PostMapping("/hit")
    public ResponseEntity<HitDto> saveHit(@Valid @RequestBody HitDto hitDto) {
        log.info("Получен POST запрос по эндпоинту /hit на создание нового HitDto {}.", hitDto);
        return new ResponseEntity<>(service.saveHit(hitDto), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getStats(
            @RequestParam("start") @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime end,
            @RequestParam(value = "uris", required = false) List<String> uris,
            @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        log.info("Получен GET запрос по эндпоинту /stats на получение статистики по посещениям.");
        return new ResponseEntity<>(service.getHits(start, end, uris, unique), HttpStatus.OK);
    }

}