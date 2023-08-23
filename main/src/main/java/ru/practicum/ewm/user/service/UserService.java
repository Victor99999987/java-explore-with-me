package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers(List<Long> ids, int from, int size);

    UserDto addNewUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);

    User getUserById(Long userId);

}
