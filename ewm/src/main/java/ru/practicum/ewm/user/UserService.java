package ru.practicum.ewm.user;

import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto);

    void delete(Long userId);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

}
