package ru.practicum.ewm.compilation.service;


import ru.practicum.ewm.compilation.dto.CompilationRequestDto;
import ru.practicum.ewm.compilation.dto.CompilationResponseDto;

import java.util.List;

public interface CompilationService {

    CompilationResponseDto create(CompilationRequestDto compilationRequestDto);

    CompilationResponseDto update(CompilationRequestDto compilationRequestDto, Long compId);

    void delete(Long compId);

    CompilationResponseDto getById(Long compId);

    List<CompilationResponseDto> getAll(Boolean pinned, Integer from, Integer size);

}