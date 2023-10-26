package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationResponseDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequestMapping("/compilations")
@RestController
@Slf4j
@RequiredArgsConstructor
public class PublicCompilationController {

    private final CompilationService compilationService;

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationResponseDto> getById(@PathVariable Long compId) {
        return new ResponseEntity<>(compilationService.getById(compId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CompilationResponseDto>> getAll(@RequestParam(required = false) Boolean pinned,
                                                               @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                               @RequestParam(defaultValue = "10") @Positive Integer size) {
        return new ResponseEntity<>(compilationService.getAll(pinned, from, size), HttpStatus.OK);
    }

}