package ru.practicum.stats.server.entity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.stats.dto.HitDto;

@Mapper(componentModel = "spring")
public interface HitMapper {

    @Mapping(target = "id", ignore = true)
    Hit mapToEntity(HitDto hitDto);

}
