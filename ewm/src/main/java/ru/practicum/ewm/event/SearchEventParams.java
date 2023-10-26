package ru.practicum.ewm.event;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.event.model.EventSort;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.utils.Constants.DATE_PATTERN;

@Data
public class SearchEventParams {

    private String text;

    private List<Long> categories;

    private Boolean paid;

    @DateTimeFormat(pattern = DATE_PATTERN)
    private LocalDateTime rangeStart;

    @DateTimeFormat(pattern = DATE_PATTERN)
    private LocalDateTime rangeEnd;

    private Boolean onlyAvailable;

    private EventSort sort;

    @PositiveOrZero
    private Integer from;

    @Positive
    private Integer size;

    public SearchEventParams(String text, List<Long> categories, Boolean paid,
                             LocalDateTime rangeStart, LocalDateTime rangeEnd,
                             Boolean onlyAvailable, EventSort sort,
                             Integer from, Integer size) {
        this.text = text;
        this.categories = categories;
        this.paid = paid;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.onlyAvailable = onlyAvailable;
        this.sort = sort;
        this.from = 0;
        this.size = 10;
    }
}
