package ru.practicum.ewm.compilation.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.ewm.utils.Marker.OnCreate;
import ru.practicum.ewm.utils.Marker.OnUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CompilationRequestDto {

    private List<Long> events = new ArrayList<>();
    private Boolean pinned;

    @NotBlank(message = "Заголовок не может быть пустым", groups = OnCreate.class)
    @Size(min = 1, max = 50, message = "Заголовок должен содержать от 1 до 50 символов",
            groups = {OnCreate.class, OnUpdate.class})
    private String title;

}
