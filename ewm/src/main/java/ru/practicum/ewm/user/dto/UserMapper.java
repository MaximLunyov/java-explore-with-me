package ru.practicum.ewm.user.dto;


import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import ru.practicum.ewm.user.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

    List<UserDto> toUserDto(Page<User> users);

    List<UserDto> toUserDto(List<User> users);

}
