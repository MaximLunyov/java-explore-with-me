package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.practicum.ewm.event.validator.annotation.EventDateAnnotationValid;
import ru.practicum.ewm.location.LocationDto;
import ru.practicum.ewm.utils.Marker.OnCreate;
import ru.practicum.ewm.utils.Marker.OnUpdate;
import ru.practicum.ewm.utils.Marker.OnUpdateAdmin;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.ewm.utils.Constants.DATE_PATTERN;


@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {

    @NotBlank(message = "Аннотация не может быть пустой", groups = OnCreate.class)
    @Size(min = 20, max = 2000, message = "Аннотация к событию должна содержать от 20 до 2000 символов",
            groups = {OnCreate.class, OnUpdate.class, OnUpdateAdmin.class})
    protected String annotation;

    @NotNull(message = "Категория не может быть нулевой", groups = OnCreate.class)
    protected Long category;

    @NotBlank(message = "Описание не может быть пустым", groups = OnCreate.class)
    @Size(min = 20, max = 7000, message = "Описание события должно содержать от 20 до 7000 символов",
            groups = {OnCreate.class, OnUpdate.class, OnUpdateAdmin.class})
    protected String description;

    @NotNull(message = "Дата события не может быть нулевой", groups = OnCreate.class)
    @JsonFormat(pattern = DATE_PATTERN)
    @EventDateAnnotationValid(isAdmin = false, groups = {OnCreate.class, OnUpdate.class})
    @EventDateAnnotationValid(isAdmin = true, groups = {OnUpdateAdmin.class})
    protected LocalDateTime eventDate;

    @Valid
    protected LocationDto location;

    protected Boolean paid;

    @PositiveOrZero(message = "Лимит участников не может быть отрицательным")
    protected Integer participantLimit;

    protected Boolean requestModeration;

    @NotBlank(message = "Заголовок не может быть пустым или null", groups = OnCreate.class)
    @Size(min = 3, max = 120, message = "Название мероприятия должно содержать от 3 до 120 символов",
            groups = {OnCreate.class, OnUpdate.class, OnUpdateAdmin.class})
    protected String title;

}

