package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import ru.practicum.ewm.event.model.EventStateAction;

@Data
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventDto extends NewEventDto {

    private EventStateAction stateAction;

}
