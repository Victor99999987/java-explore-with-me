package ru.practicum.ewm.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@Slf4j
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    List<UserDto> getAllUsers(@RequestParam(required = false) List<Long> ids,
                              @RequestParam(defaultValue = "0") int from,
                              @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос на эндпоинт {}", "GET /admin/users");
        return userService.getAllUsers(ids, from, size);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    UserDto addNewUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("Получен запрос на эндпоинт {}", "POST /admin/users");
        return userService.addNewUser(newUserRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUser(@PathVariable Long userId) {
        log.info("Получен запрос на эндпоинт {}", "DELETE /admin/users/" + userId);
        userService.deleteUser(userId);
    }

}
