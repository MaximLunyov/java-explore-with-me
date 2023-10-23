package ru.practicum.ewm.event.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.location.LocationDto;

import java.time.LocalDateTime;

import static ru.practicum.ewm.utils.Constants.DATE_PATTERN;


@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto extends EventShortDto {

    private String description;
    private LocationDto location;
    private Integer participantLimit;
    private Boolean requestModeration;
    private EventState state;
    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDateTime publishedOn;
    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDateTime createdOn;

}