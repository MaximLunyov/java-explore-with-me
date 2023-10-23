package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationRequestDto;
import ru.practicum.ewm.compilation.dto.CompilationResponseDto;
import ru.practicum.ewm.compilation.service.CompilationService;
import ru.practicum.ewm.utils.Marker.OnCreate;
import ru.practicum.ewm.utils.Marker.OnUpdate;

@Validated
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationResponseDto> create(
            @RequestBody @Validated(OnCreate.class) CompilationRequestDto compilationRequestDto) {
        return new ResponseEntity<>(compilationService.create(compilationRequestDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationResponseDto> update(
            @RequestBody @Validated(OnUpdate.class) CompilationRequestDto compilationRequestDto,
            @PathVariable Long compId) {
        return new ResponseEntity<>(compilationService.update(compilationRequestDto, compId), HttpStatus.OK);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<?> delete(@PathVariable Long compId) {
        compilationService.delete(compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
