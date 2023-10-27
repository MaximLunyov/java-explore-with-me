package ru.practicum.ewm.event.dto;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.dto.CategoryMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.location.Location;
import ru.practicum.ewm.location.LocationMapper;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.dto.UserMapper;

@Mapper(componentModel = "spring", uses = {
        UserMapper.class,
        CategoryMapper.class,
        LocationMapper.class
})
public interface EventMapper {

    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    Event toEvent(NewEventDto newEventDto, User initiator, Category category, Location location, EventState state);

    EventFullDto toEventFullDto(Event event, Long confirmedRequests, Long views);

    EventShortDto toEventShortDto(Event event, Long confirmedRequests, Long views);

}
