package ru.practicum.ewm.compilation.dto;


import lombok.*;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CompilationResponseDto {

    private Long id;
    private List<EventShortDto> events;
    private Boolean pinned;
    private String title;

}
